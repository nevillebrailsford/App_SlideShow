package applications.slideshow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.notification.NotificationCentre;
import application.storage.Storage;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SlideShowStoreTest extends BaseTest {

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
        slideShowStore = new SlideShowStore();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearSlideShows();
        NotificationCentre.clear();
        ApplicationConfiguration.clear();
        LogConfigurer.shutdown();
    }

    @Test
    void testSlideShowStoreNoSlideShows() throws Exception {
        Storage storage = initStorage();
        writeToStore(storage);
        assertTrue(storeSuccess);
        List<String> lines = Files.readAllLines(modelFile.toPath());
        assertEquals(2, lines.size());
        assertEquals("<slideshows/>", lines.get(1));
    }

    @Test
    void testSlideShowStoreOneSlideShow() throws Exception {
        assertFalse(storeSuccess);
        storeSlideShow(slideShow);
        assertTrue(storeSuccess);
        resetFlags();
        Storage storage = initStorage();
        assertFalse(storeSuccess);
        writeToStore(storage);
        assertTrue(storeSuccess);
        List<String> lines = Files.readAllLines(modelFile.toPath());
        assertEquals(4, lines.size());
        assertEquals("<slideshows>", lines.get(1));
        assertEquals("<slideshow title=\"title\"/>", lines.get(2).trim());
        assertEquals("</slideshows>", lines.get(3));
    }

    @Test
    void testSlideShowStoreOneSlideShowOneFolder() throws Exception {
        assertFalse(storeSuccess);
        storeSlideShow(slideShow);
        assertTrue(storeSuccess);
        resetFlags();
        storeFolder(slideShow, folder);
        assertTrue(storeSuccess);
        resetFlags();
        Storage storage = initStorage();
        assertFalse(storeSuccess);
        writeToStore(storage);
        assertTrue(storeSuccess);
        List<String> lines = Files.readAllLines(modelFile.toPath());
        assertEquals(8, lines.size());
        assertEquals("<slideshows>", lines.get(1));
        assertEquals("<slideshow title=\"title\">", lines.get(2).trim());
        assertEquals("<folder>", lines.get(3).trim());
        assertEquals("<path>C:\\Users\\nevil\\git\\App_SlideShow\\app.slideshow\\path</path>", lines.get(4).trim());
        assertEquals("</folder>", lines.get(5).trim());
        assertEquals("</slideshow>", lines.get(6).trim());
        assertEquals("</slideshows>", lines.get(7));
    }

    @Test
    void testSlideShowStoreTwoSlideShows() throws Exception {
        assertFalse(storeSuccess);
        storeSlideShow(slideShow);
        assertTrue(storeSuccess);
        resetFlags();
        storeSlideShow(slideShow2);
        assertTrue(storeSuccess);
        resetFlags();
        Storage storage = initStorage();
        assertFalse(storeSuccess);
        writeToStore(storage);
        assertTrue(storeSuccess);
        List<String> lines = Files.readAllLines(modelFile.toPath());
        assertEquals(5, lines.size());
        assertEquals("<slideshows>", lines.get(1));
        assertEquals("<slideshow title=\"title\"/>", lines.get(2).trim());
        assertEquals("<slideshow title=\"title2\"/>", lines.get(3).trim());
        assertEquals("</slideshows>", lines.get(4));
    }

}
