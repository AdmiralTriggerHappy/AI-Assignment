import java.util.*;

public class Simulator {
	private final int totalFrames;
	private final int quantum;
	private final boolean global;
	private final List<Process> processes;
	private final MemoryManager memory;
	private final Queue<Process> readyQueue = new LinkedList<>();
	private final List<PageFault> ioQueue = new ArrayList<>();
	private final Set<Process> needsQuantumReset = new HashSet<>();

	private int time = 0;
	private Process current = null;

	public Simulator(List<Process> processes, int totalFrames, int quantum, boolean global) {
		this.processes = new ArrayList<>(processes);
		this.totalFrames = totalFrames;
		this.quantum = quantum;
		this.global = global;
		this.memory = new MemoryManager(totalFrames, this.processes, global);

		// Add all processes to ready queue at time 0
		for (Process p : this.processes) {
			readyQueue.offer(p);
			needsQuantumReset.add(p);
		}
	}

	public void run() {
		while (!allFinished()) {
			// First, check for any completed I/O
			checkCompletedIO();

			// If CPU is idle, schedule next process
			if (current == null && !readyQueue.isEmpty()) {
				current = readyQueue.poll();
				// Only reset quantum if needed (first time or after quantum expiry)
				if (needsQuantumReset.contains(current)) {
					current.resetQuantum(quantum);
					needsQuantumReset.remove(current);
				}
			}

			// Execute current process or advance time if idle
			if (current != null) {
				executeCurrentProcess();
			} else {
				time++;
			}
		}
	}

	private void executeCurrentProcess() {
		Integer nextPage = current.peekNextPage();

		// Process finished
		if (nextPage == null) {
			current.setFinishTime(time);
			current = null;
			return;
		}

		// Check if page is in memory
		if (memory.hasPage(current, nextPage)) {
			// Page in memory - execute instruction
			current.advancePage();
			current.consumeQuantum();
			time++;

			// After time advances, check for completed I/O BEFORE handling quantum expiry
			Process quantumExpiredProcess = null;
			if (current.peekNextPage() == null) {
				current.setFinishTime(time);
				current = null;
			} else if (current.isQuantumExpired()) {
				quantumExpiredProcess = current;
				current = null;
			}

			// Check I/O completions at the new time
			checkCompletedIO();

			// Now add quantum-expired process (unblocked processes added first per rule f)
			if (quantumExpiredProcess != null) {
				readyQueue.offer(quantumExpiredProcess);
				needsQuantumReset.add(quantumExpiredProcess);
			}
		} else {
			// Page fault - process blocks (takes 0 time)
			current.recordFault(time);
			memory.requestPage(current, nextPage, time);

			// Get the frame that was allocated and pass it to PageFault
			Frame allocatedFrame = memory.getLastAllocatedFrame();
			ioQueue.add(new PageFault(current, nextPage, time, time + 4));

			current.block();
			current = null;
		}
	}

	private void checkCompletedIO() {
		List<PageFault> completedFaults = new ArrayList<>();

		Iterator<PageFault> it = ioQueue.iterator();
		while (it.hasNext()) {
			PageFault pf = it.next();
			if (pf.getCompletionTime() <= time) {
				completedFaults.add(pf);
				it.remove();
			}
		}

		completedFaults.sort(Comparator.comparingInt(PageFault::getFaultTime));

		// Unblock processes and add to ready queue
		for (PageFault pf : completedFaults) {
			Process p = pf.getProcess();
			p.unblock();
			needsQuantumReset.add(p);
			readyQueue.offer(p);
			// DO NOT add frame to queue here
		}
	}

	private boolean allFinished() {
		for (Process p : processes) {
			if (!p.isFinished()) {
				return false;
			}
		}
		return true;
	}

	public List<Process> getResults() {
		return processes;
	}
}