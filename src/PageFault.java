/*
 * Copyright (c) 2025 Andrew Wallace Student ID 3253268 For the University of Newcastle.
 * Created with the assistance of Chat-GPT and Claude Generative AI
 *
 */

/**
 * The type Page fault.
 */

public class PageFault {
	private final Process process;
	private final int page;
	private final int faultTime;
	private final int completionTime;
	private final Frame frame;

	/**
	 * Instantiates a new Page fault.
	 *
	 * @param process        the process
	 * @param page           the page
	 * @param faultTime      the fault time
	 * @param completionTime the completion time
	 * @param frame          the frame
	 */
	public PageFault(Process process, int page, int faultTime, int completionTime, Frame frame) {
		this.process = process;
		this.page = page;
		this.faultTime = faultTime;
		this.completionTime = completionTime;
		this.frame = frame;
	}

	/**
	 * Gets process.
	 *
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * Gets page.
	 *
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Gets fault time.
	 *
	 * @return the fault time
	 */
	public int getFaultTime() {
		return faultTime;
	}

	/**
	 * Gets completion time.
	 *
	 * @return the completion time
	 */
	public int getCompletionTime() {
		return completionTime;
	}

	/**
	 * Gets frame.
	 *
	 * @return the frame
	 */
	public Frame getFrame() {
		return frame;
	}
}