package applications.slideshow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.notification.NotificationCentre;
import application.notification.NotificationMonitor;
import java.io.File;
import java.util.logging.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import applications.slideshow.model.Directory;

class SlideShowManagerTest extends BaseTest {

    @TempDir
    File rootDirectory;

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
    }

    @AfterEach
    void tearDown() throws Exception {
        synchronized (waitForFinish) {
            SlideShowManager.instance().clear();
            waitForFinish.wait();
        }
        ApplicationConfiguration.clear();
        LogConfigurer.shutdown();
        NotificationCentre.clear();
    }

    @Test
    void testNotNull() {
        assertNotNull(SlideShowManager.instance());
    }

    @Test
    void testAdd() throws Exception {
        assertEquals(0, SlideShowManager.instance().slideShows().size());
        assertEquals(0, SlideShowManager.instance().allDirectories().size());
        assertEquals(0, SlideShowManager.instance().directories().size());
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
        assertEquals(1, SlideShowManager.instance().allDirectories().size());
        assertEquals(0, SlideShowManager.instance().directories().size());
    }

    @Test
    void testClear() throws Exception {
        addASlideShow(slideShow);
        assertEquals(1, SlideShowManager.instance().slideShows().size());
        clearSlideShows();
        assertEquals(0, SlideShowManager.instance().slideShows().size());
        assertEquals(0, SlideShowManager.instance().allDirectories().size());
        assertEquals(0, SlideShowManager.instance().directories().size());
    }

    // TreeModel tests

    @Test
    void testGetRoot() {
        assertNotNull(SlideShowManager.instance().getRoot());
        Directory root = (Directory) SlideShowManager.instance().getRoot();
        assertEquals("Slide shows", root.title());
    }

    @Test
    void testGetChild() throws Exception {
        addASlideShow(slideShow);
        Directory root = (Directory) SlideShowManager.instance().getRoot();
        Directory child = (Directory) SlideShowManager.instance().getChild(root, 0);
        assertNotNull(child);
        assertEquals("titlea", child.title());
    }

    @Test
    void testGetChildCount() throws Exception {
        addASlideShow(slideShow);
        Directory root = (Directory) SlideShowManager.instance().getRoot();
        assertEquals(1, SlideShowManager.instance().getChildCount(root));
    }

    @Test
    void testIsSlideSHowLeaf() throws Exception {
        addASlideShow(slideShow);
        Directory root = (Directory) SlideShowManager.instance().getRoot();
        Directory child = (Directory) SlideShowManager.instance().getChild(root, 0);
        assertTrue(SlideShowManager.instance().isLeaf(child));
    }

    @Test
    void testIsDirectoryLeaf() throws Exception {
        addASlideShow(slideShow);
        addADirectory(slideShow, directory);
        Directory root = (Directory) SlideShowManager.instance().getRoot();
        Directory child = (Directory) SlideShowManager.instance().getChild(root, 0);
        assertFalse(SlideShowManager.instance().isLeaf(child));
        assertEquals(1, child.directories().size());
        Directory leaf = child.directories().get(0);
        assertTrue(SlideShowManager.instance().isLeaf(leaf));
    }

    @Test
    void testgetIndexOfChild() throws Exception {
        addASlideShow(slideShow);
        addASlideShow(slideShow2);
        Directory root = (Directory) SlideShowManager.instance().getRoot();
        assertEquals(2, root.slideShows().size());
        assertEquals("titlea", root.slideShows().get(0).title());
        assertEquals("titleb", root.slideShows().get(1).title());
        assertEquals(0, SlideShowManager.instance().getIndexOfChild(root, root.slideShows().get(0)));
        assertEquals(1, SlideShowManager.instance().getIndexOfChild(root, root.slideShows().get(1)));
    }
}
