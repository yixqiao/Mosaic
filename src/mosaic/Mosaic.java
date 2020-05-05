package mosaic;

public class Mosaic {
	public static void main(String[] args) {
		if (args.length == 0 || !(args[0].equals("averages") || args[0].equals("build"))) {
			System.out.println("commands:");
			System.out.println("   averages");
			System.out.println("   build");
			System.exit(0);
		} else {
			String[] newArgs = new String[args.length - 1];
			for (int i = 0; i < newArgs.length; i++) {
				newArgs[i] = args[i + 1];
			}

			if (args[0].equals("averages")) {
				Averages.avgs(newArgs);
			} else if (args[0].equals("build")) {
				Build.gen(newArgs);
			}
		}
	}
}
