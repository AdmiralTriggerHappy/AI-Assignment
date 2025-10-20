import java.util.*;

public class MemoryManager {
	private final int totalFrames;
	private final boolean global;
	private final int perProcess; // only used for Fixed-Local

	private final List<Frame> frames = new ArrayList<>();
	private final Map<String, Queue<Frame>> processFrames = new HashMap<>();
	private final Queue<Frame> globalQueue = new LinkedList<>();
	private Frame lastAllocatedFrame = null;

	public MemoryManager(int totalFrames, List<Process> processes, boolean global) {
		this.totalFrames = totalFrames;
		this.global = global;
		this.perProcess = global ? -1 : totalFrames / processes.size();

		// Initialize all frames
		for (int i = 0; i < totalFrames; i++) {
			frames.add(new Frame(i));
		}

		if (!global) {
			// Pre-allocate frames to each process
			int frameIndex = 0;
			for (Process p : processes) {
				Queue<Frame> q = new LinkedList<>();
				for (int i = 0; i < perProcess && frameIndex < totalFrames; i++, frameIndex++) {
					Frame f = frames.get(frameIndex);
					q.offer(f);
				}
				processFrames.put(p.getName(), q);
			}
		}
	}

	// Check if process has page in memory
	public boolean hasPage(Process p, int page) {
		for (Frame f : frames) {
			if (!f.isFree() && f.getProcess().equals(p.getName()) && f.getPage() == page) {
				return true;
			}
		}
		return false;
	}

	// Handle page request
	public void requestPage(Process p, int page, int currentTime) {
		if (global) {
			handleGlobalRequest(p, page, currentTime);
		} else {
			handleLocalRequest(p, page, currentTime);
		}
	}

	// ------------------ GLOBAL REPLACEMENT ------------------
	private void handleGlobalRequest(Process p, int page, int currentTime) {
		Frame victim = null;
		boolean usedFreeFrame = false;

		// First, try to find a free frame
		for (Frame f : frames) {
			if (f.isFree()) {
				victim = f;
				usedFreeFrame = true;
				break;
			}
		}

		// If no free frame, use FIFO to evict
		if (victim == null) {
			victim = globalQueue.poll();
		}

		// Load page into frame
		victim.set(p.getName(), page, currentTime);
		lastAllocatedFrame = victim; // Store the allocated frame

		// Add to queue IMMEDIATELY when fault occurs (FIFO order)
		globalQueue.offer(victim);
	}

	public Frame getLastAllocatedFrame() {
		return lastAllocatedFrame;
	}

	// ------------------ LOCAL REPLACEMENT ------------------
	private void handleLocalRequest(Process p, int page, int currentTime) {
		Queue<Frame> q = processFrames.get(p.getName());

		// Find a free frame in the process's allocation
		Frame free = null;
		for (Frame f : q) {
			if (f.isFree()) {
				free = f;
				break;
			}
		}

		Frame victim;
		if (free != null) {
			victim = free;
		} else {
			// FIFO: evict the oldest loaded page among this process's frames
			victim = null;
			long oldestTime = Long.MAX_VALUE;
			for (Frame f : q) {
				if (f.getLoadTime() < oldestTime) {
					oldestTime = f.getLoadTime();
					victim = f;
				}
			}
		}

		// Load page into frame
		victim.set(p.getName(), page, currentTime);
		lastAllocatedFrame = victim;
	}
}