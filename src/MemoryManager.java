/*
 * Copyright (c) 2025 Andrew Wallace Student ID 3253268 For the University of Newcastle.
 * Created with the assistance of Chat-GPT and Claude Generative AI
 *
 */

// MemoryManager.java
import java.util.*;

/**
 * The Memory Manager
 */
public class MemoryManager {
	private final boolean global;

	private final List<Frame> frames = new ArrayList<>();
	private final Map<String, Queue<Frame>> processFrames = new HashMap<>();
	private final Queue<Frame> globalQueue = new LinkedList<>();

	/**
	 * Instantiates a new Memory manager.
	 *
	 * @param totalFrames the total frames
	 * @param processes   the processes
	 * @param global      the global
	 */
	public MemoryManager(int totalFrames, List<Process> processes, boolean global) {
		this.global = global;
		int perProcess = global ? -1 : totalFrames / processes.size();

		for (int i = 0; i < totalFrames; i++) {
			frames.add(new Frame(i));
		}

		if (!global) {
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

	/**
	 * Has page boolean.
	 *
	 * @param p    the p
	 * @param page the page
	 * @return the boolean
	 */
	public boolean hasPage(Process p, int page) {
		for (Frame f : frames) {
			if (!f.isFree() && f.getProcess().equals(p.getName()) && f.getPage() == page) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Request page frame.
	 *
	 * @param p           the p
	 * @param page        the page
	 * @param currentTime the current time
	 * @return the frame
	 */
	public Frame requestPage(Process p, int page, int currentTime) {
		if (global) {
			return handleGlobalRequest(p, page, currentTime);
		} else {
			return handleLocalRequest(p, page, currentTime);
		}
	}

	private Frame handleGlobalRequest(Process p, int page, int currentTime) {
		Frame victim = null;

		for (Frame f : frames) {
			if (f.isFree()) {
				victim = f;
				break;
			}
		}

		if (victim == null) {
			victim = globalQueue.poll();
		}

		victim.set(p.getName(), page, currentTime);

		// Don't add to queue yet - will be added when page completes loading
		return victim;
	}

	private Frame handleLocalRequest(Process p, int page, int currentTime) {
		Queue<Frame> q = processFrames.get(p.getName());

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
			victim = null;
			long oldestTime = Long.MAX_VALUE;
			for (Frame f : q) {
				if (f.getLoadTime() < oldestTime) {
					oldestTime = f.getLoadTime();
					victim = f;
				}
			}
		}

		victim.set(p.getName(), page, currentTime);
		return victim;
	}

	/**
	 * Add frame to global queue.
	 *
	 * @param f the f
	 */
	public void addFrameToGlobalQueue(Frame f) {
		if (global && !globalQueue.contains(f)) {
			globalQueue.offer(f);
		}
	}
}

