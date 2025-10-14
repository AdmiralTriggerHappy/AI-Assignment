public class Frame {
	private final int id;
	private String process = null;
	private int page = -1;
	private long loadTime = -1;

	public Frame(int id) {
		this.id = id;
	}

	public void set(String process, int page, long time) {
		this.process = process;
		this.page = page;
		this.loadTime = time;
	}

	public void clear() {
		this.process = null;
		this.page = -1;
		this.loadTime = -1;
	}

	public int getId() {
		return id;
	}

	public boolean isFree() {
		return process == null;
	}

	public String getProcess() {
		return process;
	}

	public int getPage() {
		return page;
	}

	public long getLoadTime() {
		return loadTime;
	}



}