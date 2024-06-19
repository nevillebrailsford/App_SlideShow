package applications.slideshow.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class SlideShowTest {
    static DocumentBuilderFactory documentBuilderFactory;

    Document document = null;
    SlideShow slideShow = null;
    SlideShow nullSlideShow = null;
    SlideShow slideShow2 = null;
    Folder nullFolder = null;
    Folder folder = null;
    File nullFile = null;
    File file = null;
    Element nullElement = null;
    Element element = null;
    Element folderElement = null;
    String nullPath = null;
    String path = null;
    String nullTitle = null;
    String title = "test slide show";

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    @BeforeEach
    void setUp() throws Exception {
        file = new File("This is a test path");
        path = file.getAbsolutePath();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            slideShow = new SlideShow(title);
            folder = new Folder(file);
            element = slideShow.buildElement(document);
            folderElement = folder.buildElement(document);
            slideShow2 = new SlideShow(title + " 2");
        } catch (ParserConfigurationException e) {
            throw e;
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        document = null;
        slideShow.clear();
    }

    @Test
    void testCreateWithString() {
        assertNotNull(new SlideShow("test slide show"));
    }

    @Test
    void testCreateWithElement() {
        assertNotNull(new SlideShow(element));
    }

    @Test
    void testTitleEquals() {
        assertEquals(title, slideShow.title());
    }

    @Test
    void testFoldersNotNull() {
        assertNotNull(slideShow.all());
    }

    @Test
    void testFoldersEmpty() {
        assertEquals(0, slideShow.all().size());
    }

    @Test
    void testNumberOfFolders() {
        assertEquals(0, slideShow.numberOfFolders());
    }

    @Test
    void testAddFolder() {
        assertEquals(0, slideShow.numberOfFolders());
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.numberOfFolders());
        assertEquals(0, slideShow.numberOfSlideShows());
        assertEquals(1, slideShow.all().size());
    }

    @Test
    void testAddFolderPath() {
        assertEquals(0, slideShow.numberOfFolders());
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.numberOfFolders());
        Folder f = slideShow.all().get(0);
        assertEquals(path, f.path());
    }

    @Test
    void testRemoceFolder() {
        slideShow.addFolder(folder);
        assertEquals(1, slideShow.numberOfFolders());
        slideShow.removeFolder(folder);
        assertEquals(0, slideShow.numberOfFolders());
    }

    @Test
    void testClear() {
        slideShow.addFolder(folder);
        slideShow.addFolder(folder);
        assertEquals(2, slideShow.numberOfFolders());
        slideShow.clear();
        assertEquals(0, slideShow.numberOfFolders());
    }

    @Test
    void testIsSlideShow() {
        assertTrue(slideShow.isSlideShow());
    }

    @Test
    void testIsFolder() {
        assertFalse(slideShow.isFolder());
    }

    @Test
    void testEquals() {
        SlideShow other = new SlideShow(title);
        assertTrue(slideShow.equals(other));
    }

    @Test
    void testEqualsThis() {
        assertTrue(slideShow.equals(slideShow));
    }

    @Test
    void testEqualsNull() {
        assertFalse(slideShow.equals(null));
    }

    @Test
    void testCompareToEquals() {
        SlideShow other = new SlideShow(title);
        assertTrue(slideShow.compareTo(other) == 0);
    }

    @Test
    void testCompareToEqualsThis() {
        assertTrue(slideShow.compareTo(slideShow) == 0);
    }

    @Test
    void testAddSlideShow() {
        assertEquals(0, slideShow.numberOfFolders());
        slideShow.addFolder(slideShow2);
        assertEquals(0, slideShow.numberOfFolders());
        assertEquals(1, slideShow.numberOfSlideShows());
        assertEquals(1, slideShow.all().size());
    }

    @Test
    void testInvalidCreateWithNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SlideShow(nullTitle);
        });
    }

    @Test
    void testInvalidCreateWithEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SlideShow("");
        });
    }

    @Test
    void testInvalidCreateWithNullElement() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SlideShow(nullElement, "");
        });
    }

    @Test
    void testInvalidCreateWithNullTag() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SlideShow(element, nullTitle);
        });
    }

    @Test
    void testInvalidCreateWithInvalidTag() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SlideShow(element, XMLConstants.FOLDER);
        });
    }

    @Test
    void testInvalidCreateWithElement() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SlideShow(folderElement, XMLConstants.SLIDE_SHOW);
        });
    }

    @Test
    void testCompareToNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            slideShow.compareTo(nullSlideShow);
        });
    }

}
