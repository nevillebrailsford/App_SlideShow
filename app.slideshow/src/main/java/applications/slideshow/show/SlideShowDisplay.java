package applications.slideshow.show;

import application.animation.GApplication;
import application.animation.GImage;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import applications.slideshow.gui.IApplication;

public class SlideShowDisplay extends GApplication {
    private static final int RESUME_BUTTON_WIDTH = 20;
    private static final int RESUME_BUTTON_HEIGHT = 20;
    private static final int PAUSE_BUTTON_HEIGHT = 20;
    private static final int PAUSE_BUTTON_WIDTH = 10;
    private static final int PADDING = 5;
    private static final int STOP_BUTTON_SIZE = 20;
    private static final int MARGIN = 10;
    private static final int OUTER_BOX_HEIGHT = 30;
    private static final int OUTER_BOX_WIDTH = 80;
    private static final Color UNSELECTED_BUTTON = new Color(128, 128, 128);
    private static final Color SELECTED_BUTTON = new Color(128, 255, 128);
    private static final int FRAMES_PER_SECOND = 10;
    GImage img;
    private float WIDTH;
    private float HEIGHT;
    private int displaySlideForFrames;
    private Set<Long> alreadySeen;

    Vector<String> files = null;
    int filesIndex = 0;
    File[] directories;
    boolean paused = false;
    int filesCount = 0;
    private Color stopButtonColor = UNSELECTED_BUTTON;
    private Color pauseButtonColor = UNSELECTED_BUTTON;
    private IApplication application;

    public SlideShowDisplay(File[] directories, String displaySeconds, String screenWidth, String screenHeight,
            IApplication application) {
        super();
        this.directories = directories;
        displaySlideForFrames = Integer.valueOf(displaySeconds) * FRAMES_PER_SECOND;
        WIDTH = Float.valueOf(screenWidth);
        HEIGHT = Float.valueOf(screenHeight);
        this.application = application;
        alreadySeen = new HashSet<>();
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
                application.showEnding();
                app().stop();
                frame().setVisible(false);
                frame().dispose();
            }
        });
        frame().setResizable(false);
        title("Slide Show");
        size(WIDTH, HEIGHT);
        frames(FRAMES_PER_SECOND);
        int count = countFiles(directories);
        files = new Vector<>(count);
        nameFiles(directories, files);
        filesCount = files.size();
        loadNextFile();
    }

    @Override
    public void draw() {
        image(img, 0, 0);
        title("Slide Show - showing slide " + (filesIndex + 1) + " of " + filesCount + (paused ? " - PAUSED" : ""));
        if (!paused) {
            if (frameCount > 0 && frameCount % displaySlideForFrames == 0) {
                loadNextFile();
            }
        }
        float x = determineX();
        float y = determineY();
        drawControls(x, y);
        stopButtonColor = UNSELECTED_BUTTON;
        pauseButtonColor = UNSELECTED_BUTTON;
        if (overStopButton(x, y)) {
            stopButtonColor = SELECTED_BUTTON;
            if (mousePressed) {
                stopShow();
            }
        }
        if (overPauseButton(x, y)) {
            pauseButtonColor = SELECTED_BUTTON;
            if (mousePressed) {
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
        boolean duplicate = false;
        File file = null;
        while (!duplicate) {
            file = new File(files.get(filesIndex++));
            Long checkSum = checkSum(file);
            duplicate = alreadySeen.add(checkSum);
            if (filesIndex == files.size()) {
                filesIndex = 0;
                alreadySeen = new HashSet<>();
            }
        }
        img = loadImage(file, WIDTH, HEIGHT, true);
    }

    private long checkSum(File file) {
        CheckedInputStream check = null;
        try {
            check = new CheckedInputStream(new FileInputStream(file), new CRC32());
            BufferedInputStream in = new BufferedInputStream(check);

            while (in.read() != -1) {
                // Read file in completely
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (check.getChecksum().getValue());
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
        rect(x, y, OUTER_BOX_WIDTH, OUTER_BOX_HEIGHT, 10);
    }

    private void drawStopButton(float x, float y) {
        noStroke();
        fill(stopButtonColor);
        square(x + MARGIN, y + PADDING, STOP_BUTTON_SIZE);
    }

    private void drawResumeButton(float x, float y) {
        noStroke();
        fill(pauseButtonColor);
        triangle(x + STOP_BUTTON_SIZE + 2 * MARGIN, y + PADDING, x + STOP_BUTTON_SIZE + 2 * MARGIN,
                y + RESUME_BUTTON_HEIGHT + PADDING, x + 2 * MARGIN + STOP_BUTTON_SIZE + RESUME_BUTTON_WIDTH,
                y + PADDING + (RESUME_BUTTON_WIDTH / 2));
    }

    private void drawPauseButton(float x, float y) {
        noStroke();
        fill(pauseButtonColor);
        rect(x + STOP_BUTTON_SIZE + 2 * MARGIN, y + PADDING, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, 5);
        rect(x + STOP_BUTTON_SIZE + 3 * MARGIN + PADDING, y + PADDING, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, 5);
    }

    private boolean cursorOver(float x, float y) {
        return mouseX > x && mouseX < x + OUTER_BOX_WIDTH && mouseY > y && mouseY < y + OUTER_BOX_HEIGHT;
    }

    private boolean overStopButton(float x, float y) {
        return mouseX > x + MARGIN && mouseX < x + MARGIN + STOP_BUTTON_SIZE && mouseY > y + PADDING
                && mouseY < y + STOP_BUTTON_SIZE + MARGIN;
    }

    private boolean overPauseButton(float x, float y) {
        return mouseX > x + MARGIN + STOP_BUTTON_SIZE
                && mouseX < x + 2 * MARGIN + STOP_BUTTON_SIZE + 2 * PAUSE_BUTTON_WIDTH + PADDING && mouseY > y + PADDING
                && mouseY < y + PAUSE_BUTTON_HEIGHT + PADDING;
    }

    private float determineX() {
        return (width - OUTER_BOX_WIDTH) / 2;
    }

    private float determineY() {
        return height - (OUTER_BOX_HEIGHT + MARGIN);
    }
}
