package mosaic;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class BuildImage {
	private int chunkSize;
	private int newImgScale;
	private int newChunkSize;

	private String imgPath;
	private String outPath;
	private String avgsPath;
	private int imgCount;
	private int threadCount;

	private ArrayList<String[]> rgbp = new ArrayList<String[]>();

	private BufferedImage image = null;
	private BufferedImage newImage = null;

	public BuildImage(String imgPath, String outPath, String avgsPath, int chunkSize, int newImgScale,
			int threadCount) {
		this.imgPath = imgPath;
		this.outPath = outPath;
		this.avgsPath = avgsPath;
		this.chunkSize = chunkSize;
		this.newImgScale = newImgScale;
		newChunkSize = chunkSize * newImgScale;
		this.threadCount = threadCount;

		try {
			image = ImageIO.read(new File(this.imgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class CalcChunk implements Runnable {
		private int xc, yc;

		public CalcChunk(int xc, int yc) {
			this.xc = xc;
			this.yc = yc;
		}

		public void run() {
			int r = 0, g = 0, b = 0;
			Color c;
			for (int x = 0; x < chunkSize; x++) {
				for (int y = 0; y < chunkSize; y++) {
					c = new Color(image.getRGB(xc * chunkSize + x, yc * chunkSize + y));
					r += c.getRed();
					g += c.getGreen();
					b += c.getBlue();
				}
			}
			r /= chunkSize * chunkSize;
			g /= chunkSize * chunkSize;
			b /= chunkSize * chunkSize;
			long minV = (long) 1e9;
			int minI = -1;

			for (int i = 0; i < imgCount; i++) {
				String[] curRGB = rgbp.get(i);
				long diff = 0;
				diff += (r - Integer.parseInt(curRGB[0])) * (r - Integer.parseInt(curRGB[0]));
				diff += (g - Integer.parseInt(curRGB[1])) * (g - Integer.parseInt(curRGB[1]));
				diff += (b - Integer.parseInt(curRGB[2])) * (b - Integer.parseInt(curRGB[2]));
				if (diff < minV) {
					minV = diff;
					minI = i;
				}
			}

			BufferedImage curImg = null;
			try {
				curImg = ImageIO.read(new File(rgbp.get(minI)[3]));
			} catch (IOException e) {
				e.printStackTrace();
			}

			AffineTransform scale = AffineTransform.getScaleInstance((double) newChunkSize / curImg.getWidth(),
					(double) newChunkSize / curImg.getHeight());
			AffineTransformOp op = new AffineTransformOp(scale, AffineTransformOp.TYPE_BICUBIC);
			BufferedImage newCurImg = new BufferedImage(curImg.getWidth(), curImg.getHeight(), curImg.getType());
			op.filter(curImg, newCurImg);

			for (int x = 0; x < newChunkSize; x++) {
				for (int y = 0; y < newChunkSize; y++) {
					newImage.setRGB(xc * newChunkSize + x, yc * newChunkSize + y, newCurImg.getRGB(x, y));
				}
			}
		}
	}

	public void genImage() {
		long startTime = System.nanoTime();

		newImage = new BufferedImage(image.getWidth() * newImgScale, image.getHeight() * newImgScale, image.getType());

		ExecutorService pool = Executors.newFixedThreadPool(threadCount);

		for (int xc = 0; xc < image.getWidth() / chunkSize; xc++) {
			for (int yc = 0; yc < image.getHeight() / chunkSize; yc++) {
				Runnable run = new CalcChunk(xc, yc);
				pool.execute(run);
			}
		}

		pool.shutdown();

		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			ImageIO.write(newImage, "jpg", new File(outPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(String.format("Finished building image (%.1f seconds).",
				(double) (System.nanoTime() - startTime) / 1000000000));
	}

	public void readAvgs(boolean verbose) {
		imgCount = 0;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(avgsPath)));
			while (!br.ready()) {
			}
			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				rgbp.add(s.split(","));

				imgCount++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Averages file contains " + imgCount + " averages.");
	}

}
