/**
 * COMP2240 / COMP6240 - Operating Systems
 * Assignment 3 - Paging with Virtual Memory Simulation
 *
 * Entry point for the simulation.
 * Usage: java A3 F Q process1.txt process2.txt ...
 *
 * Author: <Your Name>
 * Student ID: <Your Student ID>
 */

import java.io.*;
import java.util.*;

public class A3 {
	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Usage: java A3 <frames> <quantum> <process1> <process2> ...");
			return;
		}

		try {
			int totalFrames = Integer.parseInt(args[0]);
			int quantum = Integer.parseInt(args[1]);

			// Load process definitions from files
			List<Process> processes = new ArrayList<>();
			for (int i = 2; i < args.length; i++) {
				Process p = ProcessLoader.loadProcess(args[i]);
				processes.add(p);
			}

			// Run Fixed Allocation - Local Replacement
			List<Process> processesForFixed = deepCopyProcesses(processes);
			Simulator fixedLocalSim = new Simulator(processesForFixed, totalFrames, quantum, false);
			fixedLocalSim.run();
			ResultPrinter.print("FIFO - Fixed-Local Replacement:", fixedLocalSim.getResults());

			// Run Variable Allocation - Global Replacement
			List<Process> processesForVariable = deepCopyProcesses(processes);
			Simulator variableGlobalSim = new Simulator(processesForVariable, totalFrames, quantum, true);
			variableGlobalSim.run();
			ResultPrinter.print("FIFO - Variable-Global Replacement:", variableGlobalSim.getResults());

		} catch (NumberFormatException e) {
			System.err.println("Error: Invalid number format for frames or quantum");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("Error: Process file not found - " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Deep copy the process list to ensure each simulation has independent process states
	 */
	private static List<Process> deepCopyProcesses(List<Process> original) {
		List<Process> copy = new ArrayList<>();
		for (Process p : original) {
			copy.add(p.clone());
		}
		return copy;
	}
}