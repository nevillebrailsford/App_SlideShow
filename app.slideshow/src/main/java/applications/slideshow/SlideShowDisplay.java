package applications.slideshow;

import application.animation.GApplication;
import application.animation.GImage;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Vector;

public class SlideShowDisplay extends GApplication {
    GImage img;
    static final float WIDTH = 600;// 1280; // Toolkit.getDefaultToolkit().getScreenSize().width; // 1280; // 5184
    static final float HEIGHT = 400;// 853; // Toolkit.getDefaultToolkit().getScreenSize().height - 100; // 853; //
                                    // 3456
    Vector<String> files = null;
    int filesIndex = 0;
    File[] directories;
    boolean paused = false;
    int filesCount = 0;

    public SlideShowDisplay(File[] directories) {
        super();
        this.directories = directories;
    }

    @Override
    public void setup() {
        WindowListener[] listeners = frame().getWindowListeners();
        for (WindowListener w : listeners) {
            String name = w.getClass().getName();
            if (name.startsWith("application.animation.GApplication")) {
                frame().removeWindowListener(w);
                break;
            }
        }
        frame().addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                app().stop();
                frame().setVisible(false);
                frame().dispose();
            }
        });
        frame().setResizable(false);
        title("Slide Show");
        size(WIDTH, HEIGHT);
        frames(10);
        int count = countFiles(directories);
        files = new Vector<>(count);
        nameFiles(directories, files);
        filesCount = files.size();
        loadNextFile();
    }

    @Override
    public void draw() {
        image(img, 0, 0);
        title("Slide Show - showing slide " + (filesIndex + 1) + " of " + filesCount);
        if (!paused) {
            if (frameCount > 0 && frameCount % 100 == 0) {
                filesIndex++;
                if (filesIndex == files.size()) {
                    filesIndex = 0;
                }
                loadNextFile();
            }
        }
        float x = determineX();
        float y = determineY();
        drawControls(x, y);
        if (mousePressed) {
            if (overStopButton(x, y)) {
                stopShow();
            }
            if (overPauseButton(x, y)) {
                paused = !paused;
            }
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void stopShow() {
        frame().dispatchEvent(new WindowEvent(frame(), WindowEvent.WINDOW_CLOSING));
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

    private void drawControls(float x, float y) {
        if (cursorOver(x, y)) {
            drawOuterBox(x, y);
            drawStopButton(x, y);
            if (paused) {
                drawResumeButton(x, y);
            } else {
                drawPauseButton(x, y);
            }
        }
    }

    private void drawOuterBox(float x, float y) {
        fill(255);
        rect(x, y, 200, 50);
    }

    private void drawStopButton(float x, float y) {
        fill(0);
        square(x + 10, y + 5, 40);
    }

    private void drawResumeButton(float x, float y) {
        fill(0);
        triangle(x + 60, y + 5, x + 60, y + 45, x + 100, y + 25);
    }

    private void drawPauseButton(float x, float y) {
        fill(0);
        rect(x + 60, y + 5, 10, 40);
        rect(x + 75, y + 5, 10, 40);
    }

    private boolean cursorOver(float x, float y) {
        return mouseX > x && mouseX < x + 200 && mouseY > y && mouseY < y + 50;
    }

    private boolean overStopButton(float x, float y) {
        return mouseX > x + 10 && mouseX < x + 50 && mouseY > y + 5 && mouseY < y + 45;
    }

    private boolean overPauseButton(float x, float y) {
        return mouseX > x + 60 && mouseX < x + 85 && mouseY > y + 5 && mouseY < y + 45;
    }

    private float determineX() {
        return (width / 2) - 100;
    }

    private float determineY() {
        return height - 60;
    }
}
