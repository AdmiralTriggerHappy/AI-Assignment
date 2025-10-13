import java.util.*;

public class Process implements Cloneable {
	private final String name;
	private final Queue<Integer> pages;
	private final List<Integer> faultTimes = new ArrayList<>();
	private final int arrivalTime; // Add this

	private int finishTime = -1;
	private int remainingQuantum;
	private boolean blocked = false;

	public Process(String name, Queue<Integer> pages) {
		this(name, pages, 0);
	}

	public Process(String name, Queue<Integer> pages, int arrivalTime) {
		this.name = name;
		this.pages = new LinkedList<>(pages);
		this.arrivalTime = arrivalTime;
	}
	public String getName() {
		return name;
	}

	public boolean isFinished() {
		return pages.isEmpty();
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void block() {
		blocked = true;
	}

	public void unblock() {
		blocked = false;
	}

	public Integer peekNextPage() {
		return pages.peek();
	}

	public void advancePage() {
		pages.poll();
	}

	public void recordFault(int time) {
		faultTimes.add(time);
	}

	public List<Integer> getFaultTimes() {
		return new ArrayList<>(faultTimes);
	}

	public int getFaultCount() {
		return faultTimes.size();
	}

	public void resetQuantum(int q) {
		remainingQuantum = q;
	}

	public void consumeQuantum() {
		remainingQuantum--;
	}

	public boolean isQuantumExpired() {
		return remainingQuantum <= 0;
	}

	public int getRemainingQuantum() {
		return remainingQuantum;
	}

	public void setFinishTime(int t) {
		if (finishTime == -1) {
			finishTime = t;
		}
	}

	public int getFinishTime() {
		return finishTime;
	}

	public int getTurnaroundTime() {
		return finishTime - arrivalTime;
	}

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