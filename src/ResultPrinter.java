import java.util.*;

public class ResultPrinter {
	public static void print(String title, List<Process> procs) {
		System.out.println(title);
		System.out.println("PID\tProcess\tTurnaround Time\t#Faults\tFault Times");

		int pid = 1;
		for (Process p : procs) {
			List<Integer> faultTimes = p.getFaultTimes();
			String faultTimesStr = formatFaultTimes(faultTimes);

			System.out.printf("%d\t%s\t%d\t\t%d\t%s%n",
					pid++,
					p.getName(),
					p.getTurnaroundTime(),
					p.getFaultCount(),
					faultTimesStr);
		}
		System.out.println();
	}

	private static String formatFaultTimes(List<Integer> faultTimes) {
		if (faultTimes.isEmpty()) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder("{");
		for (int i = 0; i < faultTimes.size(); i++) {
			sb.append(faultTimes.get(i));
			if (i < faultTimes.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}
}