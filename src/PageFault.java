/*
 * Copyright (c) 2025 Andrew Wallace Student ID 3253268 For the University of Newcastle.
 * Created with the assistance of Chat-GPT and Claude Generative AI
 *
 */

/**
 * The type Page fault.
 */

public record PageFault(Process process, int page, int faultTime, int completionTime, Frame frame)
{
	/**
	 * Instantiates a new Page fault.
	 *
	 * @param process        the process
	 * @param page           the page
	 * @param faultTime      the fault time
	 * @param completionTime the completion time
	 * @param frame          the frame
	 */
	public PageFault
	{
	}

	/**
	 * Gets process.
	 *
	 * @return the process
	 */
	@Override
	public Process process()
	{
		return process;
	}

	/**
	 * Gets page.
	 *
	 * @return the page
	 */
	@Override
	public int page()
	{
		return page;
	}

	/**
	 * Gets fault time.
	 *
	 * @return the fault time
	 */
	@Override
	public int faultTime()
	{
		return faultTime;
	}

	/**
	 * Gets completion time.
	 *
	 * @return the completion time
	 */
	@Override
	public int completionTime()
	{
		return completionTime;
	}

	/**
	 * Gets frame.
	 *
	 * @return the frame
	 */
	@Override
	public Frame frame()
	{
		return frame;
	}
}