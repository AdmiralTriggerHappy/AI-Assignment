import java.io.*;
import java.util.*;

/**
 * Reads process data files and constructs Process objects.
 */
public class ProcessLoader {
	public static Process loadProcess(String filename) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String name = null;
			Queue<Integer> pages = new LinkedList<>();

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty() || line.startsWith("begin")) {
					continue;
				}

				if (line.startsWith("end")) {
					break;
				}

				// Remove all whitespace for parsing
				String cleanLine = line.replaceAll("\\s+", "");

				if (cleanLine.startsWith("name:")) {
					int endIndex = cleanLine.indexOf(';');
					if (endIndex == -1) {
						throw new IOException("Invalid format: missing semicolon after name in " + filename);
					}
					name = cleanLine.substring(5, endIndex);
				} else if (cleanLine.startsWith("page:")) {
					int endIndex = cleanLine.indexOf(';');
					if (endIndex == -1) {
						throw new IOException("Invalid format: missing semicolon after page in " + filename);
					}
					String pageNum = cleanLine.substring(5, endIndex);
					try {
						pages.add(Integer.parseInt(pageNum));
					} catch (NumberFormatException e) {
						throw new IOException("Invalid page number: " + pageNum + " in " + filename);
					}
				}
			}

			if (name == null || name.isEmpty()) {
				throw new IOException("Process name not found in " + filename);
			}

			if (pages.isEmpty()) {
				throw new IOException("No pages found for process in " + filename);
			}

			return new Process(name, pages);

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// Log or ignore close exception
				}
			}
		}
	}
}