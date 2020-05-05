package mosaic;

import org.apache.commons.cli.*;

public class Gen {
	static Options options = new Options();
	public static final int LOG_TIMES = 10;
	private static String imgPath;
	private static String outPath;
	private static String avgsPath = "avgs/avgs.txt";
	private static int threadCount = 4;

	public static void gen(String[] args) {
		options.addOption("h", "help", false, "print this message");
		options.addOption(Option.builder("p").longOpt("path").hasArg().argName("path")
				.desc("path to input image (required)").build());
		options.addOption(Option.builder("o").longOpt("output-path").hasArg().argName("path")
				.desc("path to output built image").build());
		options.addOption(Option.builder("a").longOpt("averages-path").hasArg().argName("path")
				.desc("path to file of averages").build());
		options.addOption("t", "thread-count", true, "thread count for calculating");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (cmd.hasOption("h")) {
			printHelp();
		} else {
			if (cmd.hasOption("p")) {
				imgPath = cmd.getOptionValue("p");
			} else {
				System.err.println("Path to input image is required.");
				printHelp();
			}

			if (cmd.hasOption("o")) {
				outPath = cmd.getOptionValue("o");
			} else {
				outPath = "output.jpg";
				System.out.println("No output specified, defaulting to " + outPath + ".");
			}

			if (cmd.hasOption("a")) {
				avgsPath = cmd.getOptionValue("a");
			} else {
				System.out.println("No averages path specified, defautling to " + avgsPath + ".");
			}

			if (cmd.hasOption("t")) {
				threadCount = Integer.parseInt(cmd.getOptionValue("t"));
			} else {
				System.out.println("No thread count specified, defaulting to " + threadCount + " threads.");
			}
			
			System.out.println();

			GenImage gi = new GenImage(imgPath, outPath, avgsPath, threadCount);
			gi.findAvgsNum(false);
			gi.genImage();

		}
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("mosaic", options);
		System.exit(0);
	}
}
