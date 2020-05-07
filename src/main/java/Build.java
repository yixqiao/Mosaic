import org.apache.commons.cli.*;

public class Build {
	static Options options = new Options();
	private static String imgPath;
	private static String outPath = "output.jpg";
	private static String avgsPath = "avgs/avgs.txt";
	private static int chunkSize = 10;
	private static int newImgScale = 1;
	private static int threadCount = 4;
	public static boolean electron = false;

	public static void gen(String[] args) {
		options.addOption("h", "help", false, "print this message");
		options.addOption(Option.builder("p").longOpt("image-path").hasArg().argName("path")
				.desc("path to input image (required)").build());
		options.addOption(Option.builder("o").longOpt("output-path").hasArg().argName("path")
				.desc("path to output built image").build());
		options.addOption(Option.builder("a").longOpt("averages-path").hasArg().argName("path")
				.desc("path to file of averages").build());
		options.addOption("c", "chunk-size", true, "size of each small image in original image");
		options.addOption("s", "scale", true, "factor to scale output image by");
		options.addOption("t", "thread-count", true, "thread count for calculating");
		options.addOption(Option.builder().longOpt("electron-integration").desc("do not use this argument from the terminal").build());

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		assert cmd != null;
		if (cmd.hasOption("h")) {
			printHelp();
		} else {
			if (cmd.hasOption("p"))
				imgPath = cmd.getOptionValue("p");
			else {
				System.err.println("Path to input image is required.");
				printHelp();
			}

			if (cmd.hasOption("o"))
				outPath = cmd.getOptionValue("o");
			else
				System.out.println("No output specified, defaulting to " + outPath + ".");

			if (cmd.hasOption("a"))
				avgsPath = cmd.getOptionValue("a");
			else
				System.out.println("No averages path specified, defautling to " + avgsPath + ".");

			if (cmd.hasOption("c"))
				chunkSize = Integer.parseInt(cmd.getOptionValue("c"));
			else
				System.out.println("No chunk size specified, defaulting to " + chunkSize + " pixels.");

			if (cmd.hasOption("s"))
				newImgScale = Integer.parseInt(cmd.getOptionValue("s"));
			else
				System.out.println("No scale factor specified, defaulting to scale of " + newImgScale + ".");

			if (cmd.hasOption("t")) {
				threadCount = Integer.parseInt(cmd.getOptionValue("t"));
			} else {
				System.out.println("No thread count specified, defaulting to " + threadCount + " threads.");
			}

			electron = cmd.hasOption("electron-integration");

			System.out.println();

			BuildImage gi = new BuildImage(imgPath, outPath, avgsPath, chunkSize, newImgScale, threadCount);
			gi.readAvgs(false);
			gi.genImage();

		}
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("mosaic", options);
		System.exit(0);
	}
}
