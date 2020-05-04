package mosaic;

import org.apache.commons.cli.*;

public class Average {
	static Options options = new Options();
	public static int imgCount = 202599; // 202599
	public static final int LOG_TIMES = 10;
	public static int threadCount = 4;

	public static void main(String[] args) {
		options.addOption("h", "help", false, "print this message");
		options.addOption("n", "image-num", true, "number of images to use (required)");
		options.addOption("t", "thread-count", true, "thread count for calculating");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("average", options);
		} else {
			if (cmd.hasOption("n")) {
				imgCount = Integer.parseInt(cmd.getOptionValue("n"));
			} else {
				System.err.println("No image num.");
				System.exit(1);
			}

			if (cmd.hasOption("t")) {
				threadCount = Integer.parseInt(cmd.getOptionValue("t"));
			} else {
				System.out.println("No thread count specified, defaulting to " + threadCount + " threads.");
			}
			
			new CalcAverages(imgCount, threadCount).calcAverages();

		}
	}

}
