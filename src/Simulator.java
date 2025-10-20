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
	private final Queue<Process> toAddToReadyQueue = new LinkedList<>();

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
			// Check for completed I/O - add unblocked processes to temp queue
			checkCompletedIO();

			// If current process expired quantum, add it to temp queue
			// (This happens AFTER I/O check, so unblocked processes are added first per rule f)

			// Add all pending processes from temp queue to ready queue
			while (!toAddToReadyQueue.isEmpty()) {
				readyQueue.offer(toAddToReadyQueue.poll());
			}

			// If CPU is idle, schedule next process
			if (current == null && !readyQueue.isEmpty()) {
				current = readyQueue.poll();
				if (needsQuantumReset.contains(current)) {
					current.resetQuantum(quantum);
					needsQuantumReset.remove(current);
				}
			}

			// Execute current process or advance time if idle
			if (current != null) {
				executeOneStep();
			} else {
				time++;
			}
		}
	}

	private void executeOneStep() {
		Integer nextPage = current.peekNextPage();

		// Process finished
		if (nextPage == null) {
			current.setFinishTime(time);
			current = null;
			return;
		}

		// Check if page is in memory
		if (memory.hasPage(current, nextPage)) {
			// Page HIT - execute instruction
			current.advancePage();
			current.consumeQuantum();
			time++;

			// After time advances, check for any I/O completions at this new time
			checkCompletedIO();

			// After advancing, check if done
			if (current.peekNextPage() == null) {
				current.setFinishTime(time);
				current = null;
			} else if (current.isQuantumExpired()) {
				// Quantum expired - add to temp queue
				// (I/O completions were already added via checkCompletedIO, so they're first per rule f)
				toAddToReadyQueue.offer(current);
				needsQuantumReset.add(current);
				current = null;
			}
		} else {
			// Page FAULT - record fault and initiate I/O (no time advance)
			current.recordFault(time);
			memory.requestPage(current, nextPage, time);

			// I/O takes 4 time units
			ioQueue.add(new PageFault(current, nextPage, time, time + 4));

			current.block();
			current = null;
		}
	}

	private void checkCompletedIO() {
		List<PageFault> completedFaults = new ArrayList<>();

		// Find all I/O operations that completed by current time
		Iterator<PageFault> it = ioQueue.iterator();
		while (it.hasNext()) {
			PageFault pf = it.next();
			if (pf.getCompletionTime() == time) {
				completedFaults.add(pf);
				it.remove();
			}
		}

		// Sort by fault time to maintain FIFO order
		completedFaults.sort(Comparator.comparingInt(PageFault::getFaultTime));

		// Unblock processes and add to temp queue first
		// They will be added to ready queue before quantum-expired processes
		for (PageFault pf : completedFaults) {
			Process p = pf.getProcess();
			p.unblock();
			needsQuantumReset.add(p);
			toAddToReadyQueue.offer(p);
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