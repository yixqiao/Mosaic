package mosaic;

import org.apache.commons.cli.*;

public class Averages {
	static Options options = new Options();
	public static int imgCount = 202599; // 202599
	public static final int LOG_TIMES = 10;
	public static String outPath = "avgs/avgs.txt";
	public static int threadCount = 4;

	public static void avgs(String[] args) {
		options.addOption("h", "help", false, "print this message");
		options.addOption("n", "image-num", true, "number of images to use (required)");
		options.addOption("o", "output-path", true, "path to output averages to");
		options.addOption("t", "thread-count", true, "thread count for calculating");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Could not understand commands.");
			e.printStackTrace();
			System.exit(1);
		}

		if (cmd.hasOption("h")) {
			printHelp();
		}

		if (cmd.hasOption("n")) {
			imgCount = Integer.parseInt(cmd.getOptionValue("n"));
		} else {
			System.err.println("No image num.");
			printHelp();
		}

		if (cmd.hasOption("o")) {
			outPath = cmd.getOptionValue("o");
		} else {
			System.out.println("No output path specified, defaulting to " + outPath + ".");
		}

		if (cmd.hasOption("t")) {
			threadCount = Integer.parseInt(cmd.getOptionValue("t"));
		} else {
			System.out.println("No thread count specified, defaulting to " + threadCount + " threads.");
		}

		new CalcAverages(imgCount, threadCount).calcAverages();

	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("mosaic averages", options);
		System.exit(0);
	}

}
