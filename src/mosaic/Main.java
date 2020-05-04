package mosaic;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {
	public static final int IMG_COUNT = 10000; // 2020599
	public static final int LOG_TIMES = 10;

	public static void main(String[] args) {
		calcAverages();
		genImage("lake");
	}

	public static void genImage(String imgName) {
		final int CHUNK_SIZE = 10;
		final int NEW_CHUNK_SIZE = 40;
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(String.format("img_in/%s.jpg", imgName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage newImage = new BufferedImage(image.getWidth() * NEW_CHUNK_SIZE / CHUNK_SIZE,
				image.getHeight() * NEW_CHUNK_SIZE / CHUNK_SIZE, image.getType());
		for (int xc = 0; xc < image.getWidth() / CHUNK_SIZE; xc++) {
			if (xc % (image.getWidth() / CHUNK_SIZE / LOG_TIMES) == 0)
				System.out.println(String.format("%.0f%%", (double) xc / (image.getWidth() / CHUNK_SIZE) * 100));

			for (int yc = 0; yc < image.getHeight() / CHUNK_SIZE; yc++) {
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
				int minV = 1000000000, minI = -1;

				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(new File("avgs/avgs.txt")));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				try {
					while (!br.ready()) {
					}

					for (int i = 0; i < IMG_COUNT; i++) {
						String[] rgb = br.readLine().split(",");
						int diff = 0;
						diff += Math.abs(r - Integer.parseInt(rgb[0]));
						diff += Math.abs(g - Integer.parseInt(rgb[1]));
						diff += Math.abs(b - Integer.parseInt(rgb[2]));
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

		try {
			ImageIO.write(newImage, "jpg", new File(String.format("img_out/%s.jpg", imgName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void calcAverages() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("avgs/avgs.txt")));
			for (int i = 0; i < IMG_COUNT; i++) {
				if (i % (IMG_COUNT / LOG_TIMES) == 0)
					System.out.println(String.format("%.0f%%", (double) i / IMG_COUNT * 100));

				BufferedImage image = ImageIO.read(new File(String.format("img_build/%06d.jpg", i + 1)));
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
				bw.write(String.format("%d,%d,%d\n", r / imgSize, g / imgSize, b / imgSize));
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < IMG_COUNT; i++) {

		}
	}
}
