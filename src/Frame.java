/*
 * Copyright (c) 2025 Andrew Wallace Student ID 3253268 For the University of Newcastle.
 * Created with the assistance of Chat-GPT and Claude Generative AI
 *
 */

/**
 * A Frame for the memory system
 */
public class Frame {
	private final int id;
	private String process = null;
	private int page = -1;
	private long loadTime = -1;

	/**
	 * Instantiates a new Frame.
	 *
	 * @param id the id
	 */
	public Frame(int id) {
		this.id = id;
	}

	/**
	 * Set a Frame
	 *
	 * @param process the process
	 * @param page    the page
	 * @param time    the time
	 */
	public void set(String process, int page, long time) {
		this.process = process;
		this.page = page;
		this.loadTime = time;
	}

	/**
	 * Clear A Frame
	 */
	public void clear() {
		this.process = null;
		this.page = -1;
		this.loadTime = -1;
	}

	/**
	 * Gets the frame id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Is the frame free
	 *
	 * @return the boolean
	 */
	public boolean isFree() {
		return process == null;
	}

	/**
	 * Gets process using the frame.
	 *
	 * @return the process
	 */
	public String getProcess() {
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
	 * Gets load time.
	 *
	 * @return the load time
	 */
	public long getLoadTime() {
		return loadTime;
	}



}