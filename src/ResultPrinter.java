/*
 * Copyright (c) 2025 Andrew Wallace Student ID 3253268 For the University of Newcastle.
 * Created with the assistance of Chat-GPT and Claude Generative AI
 *
 */

import java.util.*;

/**
 * The type Result printer.
 */
public class ResultPrinter {
	/**
	 * Print the results to console
	 *
	 * @param title the title
	 * @param procs the procs
	 */
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

	//Format the results
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