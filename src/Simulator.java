/*
 * Copyright (c) 2025 Andrew Wallace Student ID 3253268 For the University of Newcastle.
 * Created with the assistance of Chat-GPT and Claude Generative AI
 *
 */

// Simulator.java
import java.util.*;

/**
 * The type Simulator.
 */
public class Simulator {
	private final int quantum;
	private final boolean global;
	private final List<Process> processes;
	private final MemoryManager memory;
	private final Queue<Process> readyQueue = new LinkedList<>();
	private final List<PageFault> ioQueue = new ArrayList<>();
	private final Set<Process> needsQuantumReset = new HashSet<>();

	private int time = 0;
	private Process current = null;

	/**
	 * Instantiates a new Simulator.
	 *
	 * @param processes   the processes
	 * @param totalFrames the total frames
	 * @param quantum     the quantum
	 * @param global      the global
	 */
	public Simulator(List<Process> processes, int totalFrames, int quantum, boolean global) {
		this.processes = new ArrayList<>(processes);
		this.quantum = quantum;
		this.global = global;
		this.memory = new MemoryManager(totalFrames, this.processes, global);

		for (Process p : this.processes) {
			readyQueue.offer(p);
			needsQuantumReset.add(p);
		}
	}

	/**
	 * Run.
	 */
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

	/**
	 * Execute the current Process
	 */
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

			Process quantumExpiredProcess = null;
			if (current.peekNextPage() == null) {
				current.setFinishTime(time);
				current = null;
			} else if (current.isQuantumExpired()) {
				quantumExpiredProcess = current;
				current = null;
			}

			checkCompletedIO();

			if (quantumExpiredProcess != null) {
				readyQueue.offer(quantumExpiredProcess);
				needsQuantumReset.add(quantumExpiredProcess);
			}
		} else {
			current.recordFault(time);
			Frame allocatedFrame = memory.requestPage(current, nextPage, time);
			ioQueue.add(new PageFault(current, nextPage, time, time + 4, allocatedFrame));
			current.block();
			current = null;
		}
	}

	/**
	 * Check for IO completion
	 */
	private void checkCompletedIO() {
		List<PageFault> completedFaults = new ArrayList<>();

		Iterator<PageFault> it = ioQueue.iterator();
		while (it.hasNext()) {
			PageFault pf = it.next();
			if (pf.completionTime() <= time) {
				completedFaults.add(pf);
				it.remove();
			}
		}

		completedFaults.sort(Comparator.comparingInt(PageFault::faultTime));

		// Unblock processes FIRST (without adding frames to queue yet)
		for (PageFault pf : completedFaults) {
			Process p = pf.process();
			p.unblock();
			needsQuantumReset.add(p);
			readyQueue.offer(p);
		}

		// Add frames to queue AFTER new faults might have been processed
		for (PageFault pf : completedFaults) {
			if (global) {
				memory.addFrameToGlobalQueue(pf.frame());
			}
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

	/**
	 * Gets results.
	 *
	 * @return the results
	 */
	public List<Process> getResults() {
		return processes;
	}
}