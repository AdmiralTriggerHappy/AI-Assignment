public class PageFault {
	private final Process process;
	private final int page;
	private final int faultTime;
	private final int completionTime;

	public PageFault(Process process, int page, int faultTime, int completionTime) {
		this.process = process;
		this.page = page;
		this.faultTime = faultTime;
		this.completionTime = completionTime;
	}

	public Process getProcess() {
		return process;
	}

	public int getPage() {
		return page;
	}

	public int getFaultTime() {
		return faultTime;
	}

	public int getCompletionTime() {
		return completionTime;
	}
}