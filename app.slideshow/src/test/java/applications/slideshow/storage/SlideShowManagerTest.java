package applications.slideshow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
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
        NotificationCentre.clear();
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
        assertEquals(0, SlideShowManager.instance().slideShows().size());
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
        clearSlideShows();
        assertEquals(0, SlideShowManager.instance().slideShows().size());
    }

    @Test
    void testSlideShowWithOneSlideShow() throws Exception {
        assertEquals(0, SlideShowManager.instance().slideShows().size());
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
    }

    @Test
    void testSlideShowWithOneSlideShowOneFolder() throws Exception {
        assertEquals(0, SlideShowManager.instance().slideShows().size());
        addASlideShow(slideShow);
        addAFolder(slideShow, folder);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
    }

    @Test
    void testAddSlideShow() throws Exception {
        assertEquals(0, SlideShowManager.instance().slideShows().size());
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
    }

    @Test
    void testAddSlideShowToAnother() throws Exception {
        assertEquals(0, SlideShowManager.instance().slideShows().size());
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
        assertEquals(0, slideShow.slideShows().size());
        addASlideShowTo(slideShow, slideShow2);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
        assertEquals(1, slideShow.slideShows().size());
    }

    @Test
    void testAddFolder() throws Exception {
        assertEquals(0, SlideShowManager.instance().slideShows().size());
        addASlideShow(slideShow);
        addAFolder(slideShow, folder);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
        assertEquals(1, slideShow.folders().size());
    }

    @Test
    void testAddSlideShowNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addSlideShow(null);
        });
    }

    @Test
    void testAddSlideShowDuplicate() {
        assertThrows(IllegalArgumentException.class, () -> {
            addASlideShow(slideShow);
            SlideShowManager.instance().addSlideShow(duplicateShow);
        });
    }

    @Test
    void testRemoveSlideShowNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().removeSlideShow(null);
        });
    }

    @Test
    void testRemoveSlideShowNotKnown() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().removeSlideShow(slideShow);
        });
    }

    @Test
    void testAddSlideShowToNull1() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addSlideShowTo(null, slideShow);
        });
    }

    @Test
    void testAddSlideShowToNull2() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addSlideShowTo(slideShow, null);
        });
    }

    @Test
    void testAddSlideShowToSame() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addSlideShowTo(slideShow, slideShow);
        });
    }

    @Test
    void testAddSlideShowToNotKnown() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addSlideShowTo(slideShow, slideShow2);
        });
    }

    @Test
    void testAddSlideShowToAlreadyKnown() {
        try {
            addASlideShow(slideShow);
            addASlideShowTo(slideShow, slideShow2);
        } catch (Exception e) {
            fail(e);
        }
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addSlideShowTo(slideShow, slideShow2);
        });
    }

    @Test
    void testRemoveSlideShowFromNull1() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().removeSlideShowFrom(null, slideShow);
        });
    }

    @Test
    void testRemoveSlideShowFromNull2() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().removeSlideShowFrom(slideShow, null);
        });
    }

    @Test
    void testRemoveSlideShowFromEquals() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().removeSlideShowFrom(slideShow, slideShow);
        });
    }

    @Test
    void testRemoveSlideShowFromUnknow() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().removeSlideShowFrom(slideShow, slideShow2);
        });
    }

    @Test
    void testRemoveSlideShowFromNotStored() {
        assertThrows(IllegalArgumentException.class, () -> {
            addASlideShow(slideShow);
            SlideShowManager.instance().removeSlideShowFrom(slideShow, slideShow2);
        });
    }

    @Test
    void testAddFolderNull1() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addFolder(null, folder);
        });
    }

    @Test
    void testAddFolderNull2() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addFolder(slideShow, null);
        });
    }

    @Test
    void testAddFolderUnknown() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlideShowManager.instance().addFolder(slideShow, folder);
        });
    }

}
