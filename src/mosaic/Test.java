package mosaic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Test {
	static ArrayList<String> paths = new ArrayList<String>();

	public static void main(String[] args) {
		Consumer<? super Path> addPath = (s) -> {
			if (s.toString().endsWith(".jpg"))
				paths.add(s.toString());
		};

		try {
			Files.walk(Paths.get("./")).filter(Files::isRegularFile).forEach(addPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(String s : paths) {
			System.out.println(s);
		}
	}
}
