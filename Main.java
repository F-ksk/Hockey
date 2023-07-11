
package hockey;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] argv) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				// フレームの設定関連
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(813, 640);
				HockerPanel panel = new HockerPanel();
				frame.getContentPane().add(panel);
				frame.setVisible(true);
			}
		});
	}
}
