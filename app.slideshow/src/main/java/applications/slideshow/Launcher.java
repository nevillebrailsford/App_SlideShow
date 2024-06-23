package applications.slideshow;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class Launcher {

	private static final String HOME = "C:/Users/nevil/OneDrive/Pictures/";

	public Launcher() {
		JFileChooser fc = new JFileChooser(HOME);
		fc.setDialogTitle("Select which directories you'd like to view.");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(true);
		int option = fc.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			SlideShowDisplay sh = new SlideShowDisplay(files);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Launcher();
		});
	}

}
