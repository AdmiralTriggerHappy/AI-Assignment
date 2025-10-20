/*
 * Copyright (c) 2025 Andrew Wallace Student ID 3253268 For the University of Newcastle.
 * Created with the assistance of Chat-GPT and Claude Generative AI
 *
 */

import java.util.*;

/**
 * Implements Round Robin scheduling with ready and blocked queues.
 */
public class Scheduler {
	private final Queue<Process> readyQueue;
	private final List<Process> blockedList;

	/**
	 * Instantiates a new Scheduler.
	 */
	public Scheduler() {
		readyQueue = new LinkedList<>();
		blockedList = new ArrayList<>();
	}

	/**
	 * Add ready.
	 *
	 * @param p the p
	 */
	public void addReady(Process p) {
		if (p != null && !p.isFinished()) {
			readyQueue.offer(p);
		}
	}

	/**
	 * Add blocked.
	 *
	 * @param p the p
	 */
	public void addBlocked(Process p) {
		if (p != null && !blockedList.contains(p)) {
			blockedList.add(p);
		}
	}

	/**
	 * Remove blocked.
	 *
	 * @param p the p
	 */
	public void removeBlocked(Process p) {
		blockedList.remove(p);
	}

	/**
	 * Gets next ready.
	 *
	 * @return the next ready
	 */
	public Process getNextReady() {
		return readyQueue.poll();
	}

	/**
	 * Has ready boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasReady() {
		return !readyQueue.isEmpty();
	}

	/**
	 * Gets blocked.
	 *
	 * @return the blocked
	 */
	public List<Process> getBlocked() {
		return new ArrayList<>(blockedList);
	}

	/**
	 * Has blocked processes boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasBlockedProcesses() {
		return !blockedList.isEmpty();
	}

	/**
	 * Clear queues.
	 */
	public void clearQueues() {
		readyQueue.clear();
		blockedList.clear();
	}
}