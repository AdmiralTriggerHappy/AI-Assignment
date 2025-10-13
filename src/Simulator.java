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

		for (Process p : this.processes) {
			readyQueue.offer(p);
			needsQuantumReset.add(p);
		}
	}

	public void run() {
		while (!allFinished()) {
			checkCompletedIO();

			if (current == null && !readyQueue.isEmpty()) {
				current = readyQueue.poll();
				if (needsQuantumReset.contains(current)) {
					current.resetQuantum(quantum);
					needsQuantumReset.remove(current);
				}
			}

			if (current != null) {
				executeCurrentProcess();
			} else {
				time++;
			}
		}
	}

	private void executeCurrentProcess() {
		Integer nextPage = current.peekNextPage();


		if (nextPage == null) {
			current.setFinishTime(time);
			current = null;
			return;
		}

		if (memory.hasPage(current, nextPage)) {
			current.advancePage();
			current.consumeQuantum();
			time++;

			// IMPORTANT: After time advances, check for completed I/O BEFORE handling quantum expiry
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

			// Now add quantum-expired process (rule f: unblocked processes added first)
			if (quantumExpiredProcess != null) {
				readyQueue.offer(quantumExpiredProcess);
				needsQuantumReset.add(quantumExpiredProcess);
			}
		} else {
			current.recordFault(time);
			memory.requestPage(current, nextPage, time);
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

		for (PageFault pf : completedFaults) {
			Process p = pf.getProcess();
			p.unblock();
			needsQuantumReset.add(p);  // ADD THIS LINE - reset quantum after page fault
			readyQueue.offer(p);
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