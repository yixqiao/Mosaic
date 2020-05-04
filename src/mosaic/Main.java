package mosaic;

public class Main {
	public static final int IMG_COUNT = 202599; // 202599
	public static final int LOG_TIMES = 10;
	public static final int THREAD_COUNT = 8;

	public static void main(String[] args) {
		new CalcAverages().calcAverages();
		new GenImage("lake").genImage();
	}

}
