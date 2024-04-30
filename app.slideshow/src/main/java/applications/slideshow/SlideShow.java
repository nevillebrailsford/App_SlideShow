package applications.slideshow;

import java.io.File;
import java.util.Vector;

import application.animation.GApplication;
import application.animation.GImage;

public class SlideShow extends GApplication {
	GImage img;
	static final float WIDTH = 1024;
	static final float HEIGHT = 682;
	Vector<String> files = null;
	int filesIndex = 0;
	File[] directories;

	public SlideShow(File[] directories) {
		this.directories = directories;
	}

	@Override
	public void setup() {
		title("Slide Show");
		size(WIDTH, HEIGHT);
		frames(10);
		int count = countFiles(directories);
		files = new Vector<>(count);
		nameFiles(directories, files);
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

	private int countFiles(File[] dirs) {
		int count = 0;
		for (int i = 0; i < dirs.length; i++) {
			count += countFiles(dirs[i]);
		}
		return count;
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

	private void nameFiles(File[] dirs, Vector<String> names) {
		for (int i = 0; i < dirs.length; i++) {
			nameFiles(dirs[i], names);
		}
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
