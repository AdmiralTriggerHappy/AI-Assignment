/*
 * Copyright (c) 2025 Andrew Wallace Student ID 3253268 For the University of Newcastle.
 * Created with the assistance of Chat-GPT and Claude Generative AI
 *
 */

import java.util.*;

/**
 * A class to hold a process and its data
 */
public class Process implements Cloneable {
	private final String name;
	private final Queue<Integer> pages;
	private final List<Integer> faultTimes = new ArrayList<>();
	private final int arrivalTime; // Add this

	private int finishTime = -1;
	private int remainingQuantum;
	private boolean blocked = false;

	/**
	 * Instantiates a new Process.
	 *
	 * @param name  the name
	 * @param pages the pages
	 */
	public Process(String name, Queue<Integer> pages) {
		this(name, pages, 0);
	}

	/**
	 * Instantiates a new Process.
	 *
	 * @param name        the name
	 * @param pages       the pages
	 * @param arrivalTime the arrival time
	 */
	public Process(String name, Queue<Integer> pages, int arrivalTime) {
		this.name = name;
		this.pages = new LinkedList<>(pages);
		this.arrivalTime = arrivalTime;
	}

	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Is the process finished
	 *
	 * @return the boolean
	 */
	public boolean isFinished() {
		return pages.isEmpty();
	}

	/**
	 * Is the process blocked
	 *
	 * @return the boolean
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/**
	 * Block the process
	 */
	public void block() {
		blocked = true;
	}

	/**
	 * Unblock the process
	 */
	public void unblock() {
		blocked = false;
	}

	/**
	 * Peek next page.
	 *
	 * @return the integer
	 */
	public Integer peekNextPage() {
		return pages.peek();
	}

	/**
	 * Advance page.
	 */
	public void advancePage() {
		pages.poll();
	}

	/**
	 * Record fault.
	 *
	 * @param time the time
	 */
	public void recordFault(int time) {
		faultTimes.add(time);
	}

	/**
	 * Gets fault times.
	 *
	 * @return the fault times
	 */
	public List<Integer> getFaultTimes() {
		return new ArrayList<>(faultTimes);
	}

	/**
	 * Gets fault count.
	 *
	 * @return the fault count
	 */
	public int getFaultCount() {
		return faultTimes.size();
	}

	/**
	 * Reset quantum.
	 *
	 * @param q the q
	 */
	public void resetQuantum(int q) {
		remainingQuantum = q;
	}

	/**
	 * Consume quantum.
	 */
	public void consumeQuantum() {
		remainingQuantum--;
	}

	/**
	 * Is quantum expired.
	 *
	 * @return the boolean
	 */
	public boolean isQuantumExpired() {
		return remainingQuantum <= 0;
	}

	/**
	 * Gets remaining quantum.
	 *
	 * @return the remaining quantum
	 */
	public int getRemainingQuantum() {
		return remainingQuantum;
	}

	/**
	 * Sets finish time.
	 *
	 * @param t the t
	 */
	public void setFinishTime(int t) {
		if (finishTime == -1) {
			finishTime = t;
		}
	}

	/**
	 * Gets finish time.
	 *
	 * @return the finish time
	 */
	public int getFinishTime() {
		return finishTime;
	}

	/**
	 * Gets turnaround time.
	 *
	 * @return the turnaround time
	 */
	public int getTurnaroundTime() {
		return finishTime - arrivalTime;
	}

	/**
	 * Override the Clone process to enable deep copying of a process
	 */
	@Override
	public Process clone() {
		Queue<Integer> pagesCopy = new LinkedList<>(this.pages);
		Process copy = new Process(this.name, pagesCopy, this.arrivalTime);
		copy.finishTime = this.finishTime;
		copy.remainingQuantum = this.remainingQuantum;
		copy.blocked = this.blocked;
		return copy;
	}
}