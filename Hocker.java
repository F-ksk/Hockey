
package hockey;
import java.awt.geom.Ellipse2D;

public class Hocker extends Ellipse2D.Double implements Runnable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public double vx, vy;
	public long start, a;
	private Thread thread;

	public Hocker(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.start = System.currentTimeMillis();
		this.a = this.start;
	}

	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void update() {
		long diff = (System.currentTimeMillis() - this.a);
		this.x += this.vx * (double)diff ;
		this.y += this.vy * (double)diff ;
		this.a = System.currentTimeMillis();
	}
	@Override
	public void run() {
		for (int i = 0; i < 100000; i++) {
			update();
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
