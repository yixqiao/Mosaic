import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class CalcAverages {
	private ArrayList<String> paths;
	private String outPath;
	private int imgCount;
	private int threadCount;
	private int[][] averages;
	private int completed = 0;

	public CalcAverages(ArrayList<String> paths, String outPath, int imgCount, int threadCount) {
		this.paths = paths;
		this.outPath = outPath;
		this.imgCount = (imgCount == 0 ? paths.size() : imgCount);
		this.threadCount = threadCount;
	}

	class CalcAverage implements Runnable {
		private int imgNum;

		public CalcAverage(int imgNum) {
			this.imgNum = imgNum;
		}

		public void run() {
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File(paths.get(imgNum)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			long r = 0, g = 0, b = 0;
			Color c;
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					c = new Color(image.getRGB(x, y));
					r += c.getRed();
					g += c.getGreen();
					b += c.getBlue();
				}
			}
			int imgSize = image.getWidth() * image.getHeight();
			synchronized (averages[imgNum]) {
				averages[imgNum][0] = (int) (r / imgSize);
				averages[imgNum][1] = (int) (g / imgSize);
				averages[imgNum][2] = (int) (b / imgSize);
			}
			synchronized((Integer)completed){
				completed++;
			}
		}
	}

	public void calcAverages() {
		long startTime = System.nanoTime();

		if(Averages.electron)
			System.out.println("total " + imgCount);

		averages = new int[imgCount][3];
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);

		for (int i = 0; i < imgCount; i++) {
			Runnable run = new CalcAverage(i);
			pool.execute(run);
		}

		pool.shutdown();

		while(!pool.isTerminated()){
			try {
				TimeUnit.MILLISECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized((Integer)completed){
				System.out.println("completed " + completed);
			}
		}

		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outPath)));
			for (int i = 0; i < imgCount; i++) {
				bw.write(String.format("%d,%d,%d,%s\n", averages[i][0], averages[i][1], averages[i][2], paths.get(i)));
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(String.format("Finished calculating averages (%.1f seconds).",
				(double) (System.nanoTime() - startTime) / 1000000000));
	}
}
