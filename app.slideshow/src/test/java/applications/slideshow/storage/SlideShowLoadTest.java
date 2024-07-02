package applications.slideshow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.notification.NotificationCentre;
import application.notification.NotificationMonitor;
import application.storage.Storage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import applications.slideshow.model.Directory;

class SlideShowLoadTest extends BaseTest {

    @BeforeEach
    void setUp() throws Exception {
        resetFlags();
        ApplicationDefinition app = new ApplicationDefinition("test") {

            @Override
            public Level level() {
                return Level.OFF;
            }

        };
        ApplicationConfiguration.registerApplication(app, rootDirectory.getAbsolutePath());
        LogConfigurer.setUp();
        NotificationCentre.addListener(listener);
        if (runMonitor)
            new NotificationMonitor(System.out);
        slideShowLoad = new SlideShowLoad();
    }

    @AfterEach
    void tearDown() throws Exception {
        synchronized (waitForFinish) {
            SlideShowManager.instance().clear();
            waitForFinish.wait();
        }
        NotificationCentre.removeListener(listener);
        ApplicationConfiguration.clear();
        LogConfigurer.shutdown();
    }

    @Test
    void testSlideShowLoadOnlyRoot() throws Exception {
        assertFalse(loadSuccess);
        createValidModelFileOnlyRoot();
        loadData();
        assertTrue(SlideShowManager.instance().slideShows().size() == 0);
    }

    @Test
    void testSlideShowLoadNoSlideShows() throws Exception {
        assertFalse(loadSuccess);
        createValidModelFileNoSlideShows();
        loadData();
        assertTrue(SlideShowManager.instance().slideShows().size() == 0);
    }

    @Test
    void testSlideShowLoadOneSlideShow() throws Exception {
        assertFalse(loadSuccess);
        createValidModelFileOneSlideShow();
        loadData();
        assertTrue(SlideShowManager.instance().slideShows().size() == 1);
        Directory slideShow = SlideShowManager.instance().slideShows().get(0);
        assertEquals("title1", slideShow.title());
        assertEquals(0, slideShow.slideShows().size());
        assertEquals(0, slideShow.directories().size());
    }

    @Test
    void testSlideShowLoadOneSlideShowOneDirectory() throws Exception {
        assertFalse(loadSuccess);
        createValidModelFileOneSlideShowOneDirectory();
        loadData();
        assertTrue(SlideShowManager.instance().slideShows().size() == 1);
        Directory slideShow = SlideShowManager.instance().slideShows().get(0);
        assertEquals("title1", slideShow.title());
        assertEquals(0, slideShow.slideShows().size());
        assertEquals(1, slideShow.directories().size());
        Directory directory = slideShow.directories().get(0);
        assertEquals("C:\\Users\\nevil\\git\\App_SlideShow\\app.slideshow\\path1", directory.path().getAbsolutePath());
    }

    @Test
    void testSlideShowLoadOneSlideShowOneDirectoryOneInnerSlideShow() throws Exception {
        assertFalse(loadSuccess);
        createValidModelFileOneSlideShowOneDirectoryOneInnerSlideShow();
        loadData();
        assertTrue(SlideShowManager.instance().slideShows().size() == 1);
        Directory slideShow = SlideShowManager.instance().slideShows().get(0);
        assertEquals("title1", slideShow.title());
        assertEquals(1, slideShow.slideShows().size());
        assertEquals(1, slideShow.directories().size());
        Directory innerSlideShow = slideShow.slideShows().get(0);
        assertEquals("inner1", innerSlideShow.title());
    }

    private void loadData() throws IOException, InterruptedException {
        slideShowLoad.setFileName(modelFile.getAbsolutePath());
        Storage storage = new Storage();
        synchronized (waitForFinish) {
            storage.loadStoredData(slideShowLoad);
            waitForFinish.wait();
        }
    }

    private void createValidModelFileOnlyRoot() throws IOException {
        modelFile = new File(rootDirectory, "model.dat");
        String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        String line2 = "<root>";
        String line5 = "</root>";
        List<String> lines = Arrays.asList(line1, line2, line5);
        assertFalse(modelFile.exists());
        Files.write(modelFile.toPath(), lines);
        assertTrue(modelFile.exists());
        assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
    }

    private void createValidModelFileNoSlideShows() throws IOException {
        modelFile = new File(rootDirectory, "model.dat");
        String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        String line2 = "<root>";
        String line3 = "<slideshows>";
        String line4 = "</slideshows>";
        String line5 = "</root>";
        List<String> lines = Arrays.asList(line1, line2, line3, line4, line5);
        assertFalse(modelFile.exists());
        Files.write(modelFile.toPath(), lines);
        assertTrue(modelFile.exists());
        assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
    }

    private void createValidModelFileOneSlideShow() throws IOException {
        modelFile = new File(rootDirectory, "model.dat");
        String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        String line2 = "<root>";
        String line3 = "<slideshows>";
        String line4 = "<slideshow>";
        String line5 = "<title>title1</title>";
        String line6 = "</slideshow>";
        String line7 = "</slideshows>";
        String line8 = "</root>";
        List<String> lines = Arrays.asList(line1, line2, line3, line4, line5, line6, line7, line8);
        assertFalse(modelFile.exists());
        Files.write(modelFile.toPath(), lines);
        assertTrue(modelFile.exists());
        assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
    }

    private void createValidModelFileOneSlideShowOneDirectory() throws IOException {
        modelFile = new File(rootDirectory, "model.dat");
        String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        String line2 = "<root>";
        String line3 = "<slideshows><slideshow><title>title1</title>";
        String line4 = "<directories><directory><path>C:\\Users\\nevil\\git\\App_SlideShow\\app.slideshow\\path1</path>";
        String line5 = "</directory></directories>";
        String line6 = "</slideshow>";
        String line7 = "</slideshows>";
        String line8 = "</root>";
        List<String> lines = Arrays.asList(line1, line2, line3, line4, line5, line6, line7, line8);
        assertFalse(modelFile.exists());
        Files.write(modelFile.toPath(), lines);
        assertTrue(modelFile.exists());
        assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
    }

    private void createValidModelFileOneSlideShowOneDirectoryOneInnerSlideShow() throws IOException {
        modelFile = new File(rootDirectory, "model.dat");
        String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        String line2 = "<root>";
        String line3 = "<slideshows><slideshow><title>title1</title>";
        String line4 = " <directories><directory><path>C:\\Users\\nevil\\git\\App_SlideShow\\app.slideshow\\path1</path>";
        String line5 = " </directory></directories>";
        String line6 = " <slideshows><slideshow><title>inner1</title>";
        String line7 = " </slideshow></slideshows>";
        String line8 = "</slideshow></slideshows>";
        String line9 = "</root>";
        List<String> lines = Arrays.asList(line1, line2, line3, line4, line5, line6, line7, line8, line9);
        assertFalse(modelFile.exists());
        Files.write(modelFile.toPath(), lines);
        assertTrue(modelFile.exists());
        assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
    }

}
