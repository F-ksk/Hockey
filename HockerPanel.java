
package hockey;

import java.awt.Color;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JPanel;
import java.awt.BasicStroke;

public class HockerPanel extends JPanel implements MouseMotionListener{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private double velocityX, velocityY;
	private int prevX, prevY;
	private long prevtime;
	private int endpoint = 3, count;
	private static int ptl = 0, ptr = 0;
	public double x = 550, y = 300;
	public int r2 = 30, r1 = 20;
	private Thread repaintThread;
	private Hocker h = new Hocker((int)x-r1/2, (int)y-r1/2, r1, r1);
	private Ellipse2D.Double b = new Ellipse2D.Double(0,0,0,0);

	public HockerPanel() {

		this.addMouseMotionListener(this);
		this.repaintThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					HockerPanel.this.repaint();
					try {
						Thread.sleep(5);
					} //100FPS
					catch (InterruptedException e) {
					}
				}
			}
		});
		this.repaintThread.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		if (ptl < this.endpoint && ptr < this.endpoint) {

			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.black);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			g2.setColor(Color.white);
			g2.fill(b);
			g2.setColor(Color.green);
			double rc = 80;
			double xc = 270 + rc / 2, yc = 90 + rc / 2;
			double xc2 = 270 + rc / 2, yc2 = 430 + rc / 2;
			Ellipse2D.Double[] circles = new Ellipse2D.Double[2];
			circles[0] = new Ellipse2D.Double(xc - rc / 2, yc - rc / 2, rc, rc);
			circles[1] = new Ellipse2D.Double(xc2 - rc / 2, yc2 - rc / 2, rc, rc);
			for(Ellipse2D.Double circle : circles) {
				g2.fill(circle);
			}
			g2.setColor(Color.cyan);
			/*
			 * パネルの作成 0 50 400 750 800 815 200 50 400 750 800 400 50 400 750 800 600 50 400
			 * 750 800 640
			 */

			BasicStroke wideStroke = new BasicStroke(4.0f);
			Line2D.Double l = new Line2D.Double(400, 0, 400, 600);
			Line2D.Double[] vertical_lines = new Line2D.Double[4];
			Line2D.Double[] horizontal_lines = new Line2D.Double[6];
			vertical_lines[0] = new Line2D.Double(50, 0, 50, 200);
			vertical_lines[1] = new Line2D.Double(50, 400, 50, 600);
			vertical_lines[2] = new Line2D.Double(750, 0, 750, 200);
			vertical_lines[3] = new Line2D.Double(750, 400, 750, 600);
			horizontal_lines[0] = new Line2D.Double(0, 200, 50, 200);
			horizontal_lines[1] = new Line2D.Double(0, 400, 50, 400);
			horizontal_lines[2] = new Line2D.Double(750, 200, 800, 200);
			horizontal_lines[3] = new Line2D.Double(750, 400, 800, 400);
			horizontal_lines[4] = new Line2D.Double(50, 2, 750, 2);
			horizontal_lines[5] = new Line2D.Double(50, 600, 750, 600);
			Line2D.Double gl = new Line2D.Double(0, 200, 0, 400);
			Line2D.Double gr = new Line2D.Double(800, 200, 800, 400);
			Line2D.Double guard = new Line2D.Double(170, 220, 170, 380);

			g2.draw(l);
			for(Line2D.Double vl : vertical_lines) {
				g2.draw(vl);
			}
			for(Line2D.Double hl : horizontal_lines) {
				g2.draw(hl);
			}
			g2.draw(gl);
			g2.draw(gr);
			g2.setColor(Color.green);
			g2.setStroke(wideStroke);
			g2.draw(guard);

			g2.setColor(Color.yellow);

			double rh = h.width / 2;
			double xh = h.x + rh, yh = h.y + rh;
			double rb = b.width / 2;
			double xb = b.x + rb, yb = b.y + rb;

			/* hとぶつかったら速度を与えて動き出す */
			double d = Math.sqrt(Math.pow(xh - xb, 2) + Math.pow(yh - yb, 2));
			if (d <= rh + rb) {
				this.h.vx = this.velocityX;
				this.h.vy = this.velocityY;
			}
			/* 円との衝突判定 */
			for(Ellipse2D.Double circle : circles) {
				double circlex = circle.x + circle.width/2;
				double circley = circle.y + circle.height/2;
				double dc = Math.sqrt(Math.pow(xh - circlex, 2) + Math.pow(yh - circley, 2));
				if (dc <= rh + circle.height / 2) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							reflect(circle);
						}
					});
					thread.start();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			int judge;
			count = 0;
			for (Line2D.Double vl : vertical_lines) {
				if (count < 2) {
					count++;
					judge = judge(vl, rh, xh, yh);
					if (judge == 0) {
						this.h.vx *= Math.signum(h.vx);
					} else if (judge == 1) {
						this.h.vy *= -1;
					}
				} else {
					count++;
					judge = judge(vl, rh, xh, yh);
					if (judge == 0) {
						this.h.vx *= -1 * Math.signum(h.vx);
					} else if (judge == 1) {
						this.h.vy *= -1;
					}
				}
			}
			count = 0;
			for (Line2D.Double hl : horizontal_lines) {
				if (count % 2 == 0) {
					count++;
					judge = judge(hl, rh, xh, yh);
					if (judge == 0) {
						this.h.vy *= Math.signum(h.vy);
					} else if (judge == 1) {
						this.h.vx *= -1;
					}
				} else {
					count++;
					judge = judge(hl, rh, xh, yh);
					if (judge == 0) {
						this.h.vy *= -1 * Math.signum(h.vy);
					} else if (judge == 1) {
						this.h.vx *= -1;
					}
				}
			}
			judge = judge(gl, rh, xh, yh);
			if (judge == 0) {
				ptr += 1;
				this.h.vy = 0;
				this.h.vx = 0;
				this.h.x = 540;
				this.h.y = 290;
			}
			judge = judge(gr, rh, xh, yh);
			if (judge == 0) {
				ptl += 1;
				this.h.vy = 0;
				this.h.vx = 0;
				this.h.x = 540;
				this.h.y = 290;
			}
			judge = judge(guard, rh, xh, yh);
			if (judge == 0) {
				this.h.vx *= -1;
			} else if (judge == 1) {
				this.h.vy *= -1;
			}
			Ellipse2D.Double[] pcl = new Ellipse2D.Double[ptl];
			for(int i = 0; i < ptl;  i++) {
				double pclx = 15 + r1 / 2, pcly = 10 + 30 * i + r1 / 2;
				pcl[i] = new Ellipse2D.Double(pclx - r1 / 2, pcly - r1 / 2, r1, r1);
				g2.fill(pcl[i]);
			}
			Ellipse2D.Double[] pcr = new Ellipse2D.Double[ptr];
			for(int i = 0; i < ptr;  i++) {
				double pcrx = 765 + r1 / 2, pcry = 10 + 30 * i + r1 / 2;
				pcr[i] = new Ellipse2D.Double(pcrx - r1 / 2, pcry - r1 / 2, r1, r1);
				g2.fill(pcr[i]);
			}

			h.update();
			g2.fill(this.h);

		} else if(ptl >= this.endpoint){
			super.paintComponent(g);
			g.setColor(Color.black);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.setColor(Color.blue);
			String text = "You lose";
			String score = ptl + " : " + ptr;
			int tx = 180, ty = 250;
			Font font = new Font("Ariel", Font.BOLD, 100);
			g.setFont(font);
			g.drawString(text, tx, ty);
			g.drawString(score, tx + 110, ty + 120);

		} else {
			super.paintComponent(g);
			g.setColor(Color.black);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.setColor(Color.red);
			String text = "You win";
			String score = ptl + " : " + ptr;
			int tx = 180, ty = 250;
			Font font = new Font("Ariel", Font.BOLD, 100);
			g.setFont(font);
			g.drawString(text, tx, ty);
			g.drawString(score, tx + 110, ty + 120);
		}
	}

	private int judge(Line2D.Double l, double rb, double xb, double yb) {
		double wa = xb - l.x1, ha = yb - l.y1;
		double wb = xb - l.x2, hb = yb - l.y2;
		double wc = l.x2 - l.x1, hc = l.y2 - l.y1;
		double A = Math.sqrt(wa * wa + ha * ha);
		double B = Math.sqrt(wb * wb + hb * hb);
		double C = Math.sqrt(wc * wc + hc * hc);
		double d = Math.abs(wc * ha - wa * hc) / C;
		if (d < rb) {
			if ((wa * wc + ha * hc) * (wb * wc + hb * hc) <= 0) {
				return 0;
			} else {
				if (rb > A || rb > B) {
					return 1;
				} else {
				}
			}
		}

		return 2;
	}

	public void reflect(Ellipse2D.Double circle) {
		double rh = h.width/2, rc = circle.width/2;
		double xh = h.x + rh, yh = h.y + rh;
		double xc = circle.x + rc, yc = circle.y + rc;
		double a = Math.sqrt(Math.pow(xh - xc,2) + Math.pow(yh - yc,2));
		double b = Math.abs(xh - xc);
		double c = Math.abs(yh - yc);
		double sin = b / a;
		double cos = c / a;

		if(sin == 0) {
			this.h.vx *= -1;
		} else if (cos == 0) {
			this.h.vy *= -1;
		} else {
			if (Math.signum(xh - xc) == Math.signum(yh - yc)) {
				// 速度ベクトルを円に対しての法線ベクトルと接線ベクトルに分解
				double e = (this.h.vy * sin - this.h.vx * cos);
				double n = (-1) * (this.h.vx * sin + this.h.vy * cos);
				// 接戦ベクトルと法線ベクトルをx方向とy方向に戻す
				this.h.vx = (-1) * e * cos + n * sin;
				this.h.vy = e * sin + n * cos;
			} else {
				// 速度ベクトルを円に対しての法線ベクトルと接線ベクトルに分解
				double e = (this.h.vy * sin + this.h.vx * cos);
				double n = (-1) * (this.h.vy * cos - this.h.vx * sin);
				// 接戦ベクトルと法線ベクトルをx方向とy方向に戻す
				this.h.vx = e * cos - n * sin;
				this.h.vy = e * sin + n * cos;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int currentX = e.getX();
		int currentY = e.getY();
		long currenttime = System.currentTimeMillis();

		/* 速度を求める	*/
		if(prevtime != 0) {
			int deltaX = currentX - this.prevX;
			int deltaY = currentY - this.prevY;
			long deltatime = currenttime - this.prevtime;
			if(deltatime == 0) {
				this.velocityX = (double)deltaX / 2;
				this.velocityY = (double)deltaY / 2;
			} else {
				this.velocityX = (double)deltaX / (double)deltatime;
				this.velocityY = (double)deltaY / (double)deltatime;
			}
		}
		this.prevX = currentX;
		this.prevY = currentY;
		this.prevtime = currenttime;

		if(e.getX() >= 400 && e.getX() <= 750){
			this.x = e.getX();
		} else if(e.getX() < 400){
			this.x = 400;
		} else {
			this.x = 750;
		}
		this.y = e.getY();
		this.b.setFrame(this.x-this.r2/2, this.y-this.r2/2, this.r2, this.r2);
		repaint();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
	}
}

