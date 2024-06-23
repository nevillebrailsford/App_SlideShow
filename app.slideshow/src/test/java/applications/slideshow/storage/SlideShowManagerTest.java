package applications.slideshow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.notification.NotificationCentre;
import java.util.logging.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SlideShowManagerTest extends BaseTest {

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
    }

    @AfterEach
    void tearDown() throws Exception {
        clearSlideShows();
        ApplicationConfiguration.clear();
        LogConfigurer.shutdown();
    }

    @Test
    void testInstanceNotNull() {
        assertNotNull(SlideShowManager.instance());
        synchronized (waitForFinish) {
            waitForFinish.notifyAll();
        }
    }

    @Test
    void testClear() throws Exception {
        assertEquals(0, SlideShowManager.instance().allFolders().size());
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().allFolders().size());
        clearSlideShows();
        assertEquals(0, SlideShowManager.instance().allFolders().size());
    }

    @Test
    void testAllFoldersWithOneSlideShow() throws Exception {
        assertEquals(0, SlideShowManager.instance().allFolders().size());
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().allFolders().size());
    }

    @Test
    void testAllFoldersWithOneSlideShowOneFolder() throws Exception {
        assertEquals(0, SlideShowManager.instance().allFolders().size());
        addASlideShow(slideShow);
        addAFolder(slideShow, folder);
        assertEquals(1, SlideShowManager.instance().allFolders().size());
    }

    @Test
    void testFoldersWithOneSlideShow() throws Exception {
        assertEquals(0, SlideShowManager.instance().allFolders().size());
        addASlideShow(slideShow);
        assertEquals(0, SlideShowManager.instance().folders(slideShow).size());
    }

    @Test
    void testFoldersWithOneSlideShowOneFolder() throws Exception {
        assertEquals(0, SlideShowManager.instance().allFolders().size());
        addASlideShow(slideShow);
        assertEquals(0, SlideShowManager.instance().folders(slideShow).size());
        addAFolder(slideShow, folder);
        assertEquals(1, SlideShowManager.instance().folders(slideShow).size());
    }

    @Test
    void testAddSlideShow() throws Exception {
        assertEquals(0, SlideShowManager.instance().allFolders().size());
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().allFolders().size());
    }

    @Test
    void testAddSlideShowToAnother() throws Exception {

    }

    @Test
    void testAddFolder() throws Exception {
        assertEquals(0, SlideShowManager.instance().allFolders().size());
        addASlideShow(slideShow);
        addAFolder(slideShow, folder);
        assertEquals(1, SlideShowManager.instance().allFolders().size());
        assertEquals(1, slideShow.folders().size());
    }

}
