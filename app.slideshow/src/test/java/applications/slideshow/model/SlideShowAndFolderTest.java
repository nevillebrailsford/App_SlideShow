package applications.slideshow.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlideShowAndFolderTest {

    @TempDir
    File rootDirectory;
    SlideShow slideShow = null;
    Folder folder = null;
    String title = "test slide show";
    String path = "a test file";
    File file = null;

    @BeforeEach
    void setUp() throws Exception {
        slideShow = new SlideShow(title);
        folder = new Folder(new File(rootDirectory, path));
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void test1() {
        assertNull(null);
        assertNotNull(new Object());
        assertTrue(true);
        assertFalse(false);
        assertEquals("a", "a");
    }

    @Test
    void testAllWithOneFolder() {
        assertEquals(0, slideShow.all().size());
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.all().size());
    }

    @Test
    void testAllWithOneSlideShow() {
        assertEquals(0, slideShow.all().size());
        slideShow.addFolder(new SlideShow(title));
        assertEquals(1, slideShow.all().size());
    }

    @Test
    void testAllWithOneSlideShowAndOneFolder() {
        assertEquals(0, slideShow.all().size());
        slideShow.addFolder(new SlideShow(title));
        slideShow.addFolder(folder);
        assertEquals(2, slideShow.all().size());
    }

    @Test
    void testFolderWithOneFolder() {
        assertEquals(0, slideShow.folders().size());
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.folders().size());
    }

    @Test
    void testFoldersWithOneSlideShow() {
        assertEquals(0, slideShow.folders().size());
        slideShow.addFolder(new SlideShow(title));
        assertEquals(0, slideShow.folders().size());
    }

    @Test
    void testFoldersWithOneSlideShowAndOneFolder() {
        assertEquals(0, slideShow.folders().size());
        slideShow.addFolder(new SlideShow(title));
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.folders().size());
    }

    @Test
    void testSlideShowWithOneFolder() {
        assertEquals(0, slideShow.slideShows().size());
        slideShow.addFolder(folder);
        assertEquals(0, slideShow.slideShows().size());
    }

    @Test
    void testSlideShowWithOneSlideShow() {
        assertEquals(0, slideShow.slideShows().size());
        slideShow.addFolder(new SlideShow(title));
        assertEquals(1, slideShow.slideShows().size());
    }

    @Test
    void testSlideShowWithOneSlideShowAndOneFolder() {
        assertEquals(0, slideShow.slideShows().size());
        slideShow.addFolder(new SlideShow(title));
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.slideShows().size());
    }

    @Test
    void testNumberOfSlideShowsWithOneFolder() {
        assertEquals(0, slideShow.numberOfSlideShows());
        slideShow.addFolder(folder);
        assertEquals(0, slideShow.numberOfSlideShows());
    }

    @Test
    void testNumberOfSlideShowsWithOneSlideShow() {
        assertEquals(0, slideShow.numberOfSlideShows());
        slideShow.addFolder(new SlideShow(title));
        assertEquals(1, slideShow.numberOfSlideShows());
    }

    @Test
    void testNumberOfSlideShowsWithOneSlideShowAndOneFolder() {
        assertEquals(0, slideShow.numberOfSlideShows());
        slideShow.addFolder(new SlideShow(title));
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.numberOfSlideShows());
    }

    @Test
    void testNumberOfFoldersWithOneFolder() {
        assertEquals(0, slideShow.numberOfFolders());
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.numberOfFolders());
    }

    @Test
    void testNumberOfFoldersWithOneSlideShow() {
        assertEquals(0, slideShow.numberOfFolders());
        slideShow.addFolder(new SlideShow(title));
        assertEquals(0, slideShow.numberOfFolders());
    }

    @Test
    void testNumberOfFoldersWithOneSlideShowAndOneFolder() {
        assertEquals(0, slideShow.numberOfFolders());
        slideShow.addFolder(new SlideShow(title));
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.numberOfFolders());
    }

    @Test
    void testTotalNumberOfFolders() {
        assertEquals(0, slideShow.totalNumberOfFolders());
        slideShow.addFolder(folder);
        SlideShow show2 = new SlideShow("title2");
        show2.addFolder(folder);
        slideShow.addFolder(show2);
        assertEquals(2, slideShow.totalNumberOfFolders());
    }

    @Test
    void testTotalFolders() {
        assertEquals(0, slideShow.totalFolders().size());
        slideShow.addFolder(folder);
        SlideShow show2 = new SlideShow("title2");
        show2.addFolder(folder);
        slideShow.addFolder(show2);
        assertEquals(2, slideShow.totalFolders().size());
    }

    @Test
    void testFiles() {
        assertEquals(0, slideShow.totalFolders().size());
        slideShow.addFolder(folder);
        SlideShow show2 = new SlideShow("title2");
        show2.addFolder(folder);
        slideShow.addFolder(show2);
        assertEquals(2, slideShow.totalFolders().size());
        assertEquals(2, slideShow.files().size());
    }

}
