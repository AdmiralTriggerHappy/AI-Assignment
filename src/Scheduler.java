import java.util.*;

/**
 * Implements Round Robin scheduling with ready and blocked queues.
 */
public class Scheduler {
	private final Queue<Process> readyQueue;
	private final List<Process> blockedList;

	public Scheduler() {
		readyQueue = new LinkedList<>();
		blockedList = new ArrayList<>();
	}

	public void addReady(Process p) {
		if (p != null && !p.isFinished()) {
			readyQueue.offer(p);
		}
	}

	public void addBlocked(Process p) {
		if (p != null && !blockedList.contains(p)) {
			blockedList.add(p);
		}
	}

	public void removeBlocked(Process p) {
		blockedList.remove(p);
	}

	public Process getNextReady() {
		return readyQueue.poll();
	}

	public boolean hasReady() {
		return !readyQueue.isEmpty();
	}

	public List<Process> getBlocked() {
		return new ArrayList<>(blockedList);
	}

	public boolean hasBlockedProcesses() {
		return !blockedList.isEmpty();
	}

	public void clearQueues() {
		readyQueue.clear();
		blockedList.clear();
	}
}