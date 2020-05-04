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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class GenImage {
	private int CHUNK_SIZE = 10;
	private int NEW_IMG_SCALE = 4;
	private int NEW_CHUNK_SIZE = CHUNK_SIZE * NEW_IMG_SCALE;

	private String imgPath;
	private BufferedImage image = null;
	private BufferedImage newImage = null;

	public GenImage(String imgPath) {
		this.imgPath = imgPath;
		try {
			image = ImageIO.read(new File(String.format("img_in/%s.jpg", imgPath)));
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
			for (int x = 0; x < CHUNK_SIZE; x++) {
				for (int y = 0; y < CHUNK_SIZE; y++) {
					c = new Color(image.getRGB(xc * CHUNK_SIZE + x, yc * CHUNK_SIZE + y));
					r += c.getRed();
					g += c.getGreen();
					b += c.getBlue();
				}
			}
			r /= CHUNK_SIZE * CHUNK_SIZE;
			g /= CHUNK_SIZE * CHUNK_SIZE;
			b /= CHUNK_SIZE * CHUNK_SIZE;
			long minV = (long) 1e9;
			int minI = -1;

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(new File("avgs/avgs.txt")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {
				while (!br.ready()) {
				}

				for (int i = 0; i < Main.IMG_COUNT; i++) {
					String[] rgb = br.readLine().split(",");
					long diff = 0;
					diff += (r - Integer.parseInt(rgb[0])) * (r - Integer.parseInt(rgb[0]));
					diff += (g - Integer.parseInt(rgb[1])) * (g - Integer.parseInt(rgb[1]));
					diff += (b - Integer.parseInt(rgb[2])) * (b - Integer.parseInt(rgb[2]));
					if (diff < minV) {
						minV = diff;
						minI = i;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			BufferedImage curImg = null;
			try {
				curImg = ImageIO.read(new File(String.format("img_build/%06d.jpg", minI + 1)));
			} catch (IOException e) {
				e.printStackTrace();
			}

			AffineTransform scale = AffineTransform.getScaleInstance((double) NEW_CHUNK_SIZE / curImg.getWidth(),
					(double) NEW_CHUNK_SIZE / curImg.getHeight());
			AffineTransformOp op = new AffineTransformOp(scale, AffineTransformOp.TYPE_BICUBIC);
			BufferedImage newCurImg = new BufferedImage(curImg.getWidth(), curImg.getHeight(), curImg.getType());
			op.filter(curImg, newCurImg);

			for (int x = 0; x < NEW_CHUNK_SIZE; x++) {
				for (int y = 0; y < NEW_CHUNK_SIZE; y++) {
					newImage.setRGB(xc * NEW_CHUNK_SIZE + x, yc * NEW_CHUNK_SIZE + y, newCurImg.getRGB(x, y));
				}
			}
		}
	}

	public void genImage() {
		long startTime = System.nanoTime();

		newImage = new BufferedImage(image.getWidth() * NEW_IMG_SCALE, image.getHeight() * NEW_IMG_SCALE,
				image.getType()); // Create new image

		ExecutorService pool = Executors.newFixedThreadPool(Main.THREAD_COUNT);

		for (int xc = 0; xc < image.getWidth() / CHUNK_SIZE; xc++) {
			for (int yc = 0; yc < image.getHeight() / CHUNK_SIZE; yc++) {
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
			ImageIO.write(newImage, "jpg", new File(String.format("img_out/%s.jpg", imgPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(String.format("Finished building image (%.1f seconds).",
				(double) (System.nanoTime() - startTime) / 1000000000));
	}
}
