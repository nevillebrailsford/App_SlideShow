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
import applications.slideshow.model.SlideShow;

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
    void testBankReadNoSlideShows() throws Exception {
        assertFalse(loadSuccess);
        createValidModelFileNoSlideShows();
        loadData();
        assertTrue(SlideShowManager.instance().slideShows().size() == 0);
    }

    @Test
    void testBankReadOneSlideShow() throws Exception {
        assertFalse(loadSuccess);
        createValidModelFileOneSlideShow();
        loadData();
        assertTrue(SlideShowManager.instance().slideShows().size() == 1);
        assertEquals("slide show one", SlideShowManager.instance().slideShows().get(0).title());
    }

    @Test
    void testBankReadOneSlideShowOneFolder() throws Exception {
        assertFalse(loadSuccess);
        createValidModelFileOneSlideShowOneFolder();
        loadData();
        assertTrue(SlideShowManager.instance().slideShows().size() == 1);
        assertEquals("title", SlideShowManager.instance().slideShows().get(0).title());
        assertTrue(SlideShowManager.instance().allFolders().size() == 1);
        SlideShow testShow = SlideShowManager.instance().slideShows().get(0);
        System.out.println(testShow.folders().size());
    }

    private void createValidModelFileNoSlideShows() throws IOException {
        modelFile = new File(rootDirectory, "model.dat");
        String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        String line2 = "<slideshows>";
        String line3 = "</slideshows>";
        List<String> lines = Arrays.asList(line1, line2, line3);
        assertFalse(modelFile.exists());
        Files.write(modelFile.toPath(), lines);
        assertTrue(modelFile.exists());
        assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
    }

    private void createValidModelFileOneSlideShow() throws IOException {
        modelFile = new File(rootDirectory, "model.dat");
        String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        String line2 = "<slideshows>";
        String line3 = "<slideshow title=\"slide show one\"/>";
        String line4 = "</slideshows>";
        List<String> lines = Arrays.asList(line1, line2, line3, line4);
        assertFalse(modelFile.exists());
        Files.write(modelFile.toPath(), lines);
        assertTrue(modelFile.exists());
        assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
    }

    private void createValidModelFileOneSlideShowOneFolder() throws IOException {
        modelFile = new File(rootDirectory, "model.dat");
        String line1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        String line2 = "<slideshows>";
        String line3 = "<slideshow title=\"title\">";
        String line4 = "<folder>";
        String line5 = "<path>C:\\\\Users\\\\nevil\\\\git\\\\App_SlideShow\\\\app.slideshow\\\\path</path>";
        String line6 = "</folder>";
        String line7 = "</slideshow>";
        String line8 = "</slideshows>";
        List<String> lines = Arrays.asList(line1, line2, line3, line4, line5, line6, line7, line8);
        assertFalse(modelFile.exists());
        Files.write(modelFile.toPath(), lines);
        assertTrue(modelFile.exists());
        assertLinesMatch(lines, Files.readAllLines(modelFile.toPath()));
    }

    private void loadData() throws IOException, InterruptedException {
        slideShowLoad.setFileName(modelFile.getAbsolutePath());
        Storage storage = new Storage();
        synchronized (waitForFinish) {
            storage.loadStoredData(slideShowLoad);
            waitForFinish.wait();
        }
    }

}
