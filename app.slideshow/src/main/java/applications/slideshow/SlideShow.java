package applications.slideshow;

import java.io.File;
import java.util.Vector;

import javax.swing.SwingUtilities;

import application.animation.GApplication;
import application.animation.GImage;

public class SlideShow extends GApplication {
	GImage img;
	static final float WIDTH = 1024;
	static final float HEIGHT = 682;
	Vector<String> files = null;
	int filesIndex = 0;

	public SlideShow() {
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new SlideShow();
		});
	}

	@Override
	public void setup() {
		title("Slide Show");
		size(WIDTH, HEIGHT);
		frames(10);
		File dir = new File("C:\\Users\\nevil\\OneDrive\\Pictures\\Jersey trip 2024");
		int count = countFiles(dir);
		files = new Vector<>(count);
		nameFiles(dir, files);
		loadNextFile();
	}

	@Override
	public void draw() {
		image(img, 0, 0);
		if (frameCount > 0 && frameCount % 100 == 0) {
			filesIndex++;
			if (filesIndex == files.size() - 1) {
				filesIndex = 0;
			}
			loadNextFile();
		}
	}

	private void loadNextFile() {
		File file = new File(files.get(filesIndex));
		img = loadImage(file, WIDTH, HEIGHT, false);
	}

	private int countFiles(File dir) {
		int count = 0;
		File[] fileList = dir.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile()) {
				if (fileList[i].getName().toLowerCase().endsWith(".jpg")) {
					count++;
				}
			} else {
				count += countFiles(fileList[i]);
			}
		}
		return count;
	}

	private void nameFiles(File dir, Vector<String> names) {
		File[] fileList = dir.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile()) {
				if (fileList[i].getName().toLowerCase().endsWith(".jpg")) {
					names.add(fileList[i].getAbsolutePath());
				}
			} else {
				nameFiles(fileList[i], names);
			}
		}
	}
}
