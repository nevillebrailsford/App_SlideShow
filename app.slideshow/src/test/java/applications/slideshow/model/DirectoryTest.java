package applications.slideshow.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class DirectoryTest {
    static DocumentBuilderFactory documentBuilderFactory;

    static final String test = "test";
    static final String title = "title";

    @TempDir
    File home;

    Directory slideShow;
    Directory directory;
    File path;
    Document document = null;
    Element directoryElement = null;
    Element slideShowElement = null;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    @BeforeEach
    void setUp() throws Exception {
        slideShow = new Directory(title);
        path = new File(home, test);
        directory = new Directory(path);
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            slideShowElement = slideShow.buildElement(document);
            directoryElement = directory.buildElement(document);
        } catch (ParserConfigurationException e) {
            throw e;
        }
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testConstructorTitle() {
        assertNotNull(slideShow);
        validateSlideShow(slideShow);
    }

    @Test
    void testConstructorPath() {
        assertNotNull(directory);
        validateDirectory(directory);
    }

    @Test
    void testDirectoryBuiltElement() {
        assertNotNull(directoryElement);
        String filePath = path.getAbsolutePath();
        assertEquals(filePath,
                directoryElement.getElementsByTagName(XMLConstants.DIRECTORY_PATH).item(0).getTextContent());
    }

    @Test
    void testConstructorDirectoryElement() {
        Directory dir = new Directory(directoryElement);
        validateDirectory(dir);
    }

    @Test
    void testSlideShowBuildElement() {
        assertNotNull(slideShowElement);
        assertEquals(title,
                slideShowElement.getElementsByTagName(XMLConstants.SLIDE_SHOW_TITLE).item(0).getTextContent());
    }

    @Test
    void testConstructorSlideShowElement() {
        Directory dir = new Directory(slideShowElement);
        validateSlideShow(dir);
    }

    @Test
    void testAddDirectoryToSlideShow() {
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
        assertTrue(slideShow.add(directory));
        assertEquals(1, slideShow.totalNumberOfDirectories());
        assertEquals(1, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
    }

    @Test
    void testAddDirectoryToDirectory() {
        assertEquals(0, directory.totalNumberOfDirectories());
        assertEquals(0, directory.numberOfDirectories());
        assertEquals(0, directory.numberOfSlideShows());
        Directory dir = new Directory(new File("test"));
        assertFalse(directory.add(dir));
        assertEquals(0, directory.totalNumberOfDirectories());
        assertEquals(0, directory.numberOfDirectories());
        assertEquals(0, directory.numberOfSlideShows());
    }

    @Test
    void testAddSlideShowToDirectory() {
        assertEquals(0, directory.totalNumberOfDirectories());
        assertEquals(0, directory.numberOfDirectories());
        assertEquals(0, directory.numberOfSlideShows());
        assertFalse(directory.add(slideShow));
        assertEquals(0, directory.totalNumberOfDirectories());
        assertEquals(0, directory.numberOfDirectories());
        assertEquals(0, directory.numberOfSlideShows());
    }

    @Test
    void testAddSlideShowToSlideShow() {
        Directory ss2 = new Directory("title two");
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
        assertTrue(slideShow.add(ss2));
        assertEquals(1, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(1, slideShow.numberOfSlideShows());
    }

    @Test
    void testAddSlideShowToSameSlideShow() {
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
        assertFalse(slideShow.add(slideShow));
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
    }

    @Test
    void testAddSlideShowToSlideShowTwice() {
        Directory ss2 = new Directory("title two");
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
        assertTrue(slideShow.add(ss2));
        assertEquals(1, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(1, slideShow.numberOfSlideShows());
        assertFalse(slideShow.add(ss2));
        assertEquals(1, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(1, slideShow.numberOfSlideShows());
    }

    @Test
    void testAddSlideShowToSlideShowTwiceNextLevel() {
        Directory ss2 = new Directory("title two");
        Directory ss3 = new Directory("title three");
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
        assertTrue(slideShow.add(ss2));
        assertTrue(ss2.add(ss3));
        assertEquals(1, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(1, slideShow.numberOfSlideShows());
        assertFalse(slideShow.add(ss3));
        assertEquals(1, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(1, slideShow.numberOfSlideShows());
    }

    @Test
    void testRemoveDirectoryFromEmptySlideShow() {
        assertFalse(slideShow.remove(directory));
    }

    @Test
    void testRemoveDirectoryFromSlideShow() {
        assertTrue(slideShow.add(directory));
        assertEquals(1, slideShow.totalNumberOfDirectories());
        assertEquals(1, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
        assertTrue(slideShow.remove(directory));
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
    }

    @Test
    void testRemoveSlideShowFromDirectory() {
        assertFalse(directory.remove(slideShow));
    }

    @Test
    void testClear() {
        assertTrue(slideShow.add(directory));
        assertEquals(1, slideShow.totalNumberOfDirectories());
        assertEquals(1, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
        slideShow.clear();
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
    }

    @Test
    void testEqualSlideShows() {
        assertTrue(slideShow.equals(slideShow));
        Directory ss2 = new Directory(title);
        assertTrue(slideShow.equals(ss2));
        assertTrue(ss2.equals(slideShow));
    }

    @Test
    void testEqualDirectories() {
        assertTrue(directory.equals(directory));
        Directory dir = new Directory(path);
        assertTrue(directory.equals(dir));
        assertTrue(dir.equals(directory));
    }

    @Test
    void testEqualSlideShowDirectory() {
        assertFalse(directory.equals(slideShow));
        assertFalse(slideShow.equals(directory));
    }

    @Test
    void testCompareToSlideShowDirectory() {
        assertTrue(slideShow.compareTo(directory) > 0);
        assertTrue(directory.compareTo(slideShow) < 0);
    }

    @Test
    void testCompareToSameDirectory() {
        assertTrue(slideShow.compareTo(slideShow) == 0);
        assertTrue(directory.compareTo(directory) == 0);
    }

    @Test
    void testCompareToSlideShow() {
        Directory dir1 = new Directory("titlea");
        Directory dir2 = new Directory("titleb");
        Directory dir3 = new Directory("titlec");
        assertTrue(dir1.compareTo(dir2) > 0);
        assertTrue(dir1.compareTo(dir3) > 0);
        assertTrue(dir2.compareTo(dir3) > 0);
        assertTrue(dir3.compareTo(dir2) < 0);
        assertTrue(dir3.compareTo(dir1) < 0);
        assertTrue(dir2.compareTo(dir1) < 0);
    }

    private void validateSlideShow(Directory slideShow) {
        assertTrue(slideShow.isSlideShow());
        assertFalse(slideShow.isDirectory());
        assertEquals(title, slideShow.title());
        assertNotNull(slideShow.allDirectories());
        assertEquals(0, slideShow.allDirectories().size());
        assertNotNull(slideShow.slideShows());
        assertNotNull(slideShow.directories());
        assertEquals(0, slideShow.slideShows().size());
        assertEquals(0, slideShow.directories().size());
        assertEquals(0, slideShow.totalNumberOfDirectories());
        assertEquals(0, slideShow.numberOfSlideShows());
        assertEquals(0, slideShow.numberOfDirectories());
    }

    private void validateDirectory(Directory directory) {
        assertTrue(directory.isDirectory());
        assertFalse(directory.isSlideShow());
        assertEquals(path, directory.path());
        assertNull(directory.allDirectories());
        assertNull(directory.slideShows());
        assertNull(directory.directories());
        assertEquals(0, directory.totalNumberOfDirectories());
        assertEquals(0, directory.numberOfSlideShows());
        assertEquals(0, directory.numberOfDirectories());
    }

}
