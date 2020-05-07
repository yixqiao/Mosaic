import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.apache.commons.cli.*;

public class Averages {
	static Options options = new Options();
	public static ArrayList<String> paths = new ArrayList<>();
	public static int imgCount = 0; // 202599
	public static String outPath = "avgs.txt";
	public static int threadCount = 4;
	public static boolean electron = false;

	

	public static void avgs(String[] args) {
		options.addOption("h", "help", false, "print this message");
		options.addOption("i", "input-path", true, "directory to get images from (required)");
		options.addOption("n", "image-num", true, "limit number of images");
		options.addOption("o", "output-path", true, "path to output averages to");
		options.addOption("t", "thread-count", true, "thread count for calculating");
		options.addOption(Option.builder().longOpt("electron-integration").desc("do not use this argument from the terminal").build());

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Could not understand commands.");
			e.printStackTrace();
			System.exit(1);
		}

		if (cmd.hasOption("h"))
			printHelp();

		if (cmd.hasOption("i"))
			getPaths(cmd.getOptionValue("i"));
		else {
			System.err.println("No directory for images specified.");
			printHelp();
		}

		if (cmd.hasOption("n"))
			imgCount = Integer.parseInt(cmd.getOptionValue("n"));

		if (cmd.hasOption("o"))
			outPath = cmd.getOptionValue("o");
		else
			System.out.println("No output path specified, defaulting to " + outPath + ".");

		if (cmd.hasOption("t"))
			threadCount = Integer.parseInt(cmd.getOptionValue("t"));
		else
			System.out.println("No thread count specified, defaulting to " + threadCount + " threads.");

		electron = cmd.hasOption("electron-integration");

		new CalcAverages(paths, outPath, imgCount, threadCount).calcAverages();

	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("mosaic averages", options);
		System.exit(0);
	}

	private static void getPaths(String rootPath) {
		Consumer<? super Path> addPath = (s) -> {
			if (s.toString().endsWith(".jpg") || s.toString().endsWith(".png"))
				paths.add(s.toString());
		};

		try {
			Files.walk(Paths.get(rootPath)).filter(Files::isRegularFile).forEach(addPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
