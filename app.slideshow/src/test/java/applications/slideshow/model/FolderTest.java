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
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class FolderTest {
    static DocumentBuilderFactory documentBuilderFactory;

    @TempDir
    File rootDirectory;
    Document document = null;
    SlideShow slideShow = null;
    SlideShow nullSlideShow = null;
    Folder nullFolder = null;
    Folder folder = null;
    File nullFile = null;
    File file = null;
    Element nullElement = null;
    Element element = null;
    Element slideShowElement = null;
    String nullPath = null;
    String path = null;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    @BeforeEach
    void setUp() throws Exception {
        file = new File(rootDirectory, "This is a test path");
        path = file.getAbsolutePath();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            folder = new Folder(file);
            element = folder.buildElement(document);
            slideShow = new SlideShow("test");
            slideShowElement = slideShow.buildElement(document);
        } catch (ParserConfigurationException e) {
            throw e;
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        document = null;
    }

    @Test
    void testValidCreateWithFile() {
        assertNotNull(new Folder(file));
    }

    @Test
    void testValidCreateWithElement() {
        assertNotNull(element);
    }

    @Test
    void testValidPath() {
        assertEquals(folder.path(), path);
    }

    @Test
    void testIsFolder() {
        assertTrue(folder.isFolder());
    }

    @Test
    void testIsSlideShow() {
        assertFalse(folder.isSlideShow());
    }

    @Test
    void testValidBuildElement() {
        String folderPath = element.getElementsByTagName(XMLConstants.FOLDER_PATH).item(0).getTextContent();
        assertEquals(path, folderPath);
    }

    @Test
    void testEquals() {
        Folder other = new Folder(file);
        assertTrue(folder.equals(other));
    }

    @Test
    void testEqualsThis() {
        assertTrue(folder.equals(folder));
    }

    @Test
    void testEqualsNull() {
        assertFalse(folder.equals(null));
    }

    @Test
    void testCompareToEquals() {
        Folder other = new Folder(file);
        assertTrue(folder.compareTo(other) == 0);
    }

    @Test
    void testCompareToEqualsThis() {
        assertTrue(folder.compareTo(folder) == 0);
    }

    @Test
    void testFile() {
        assertNotNull(folder.file());
        assertEquals(folder.file().getAbsolutePath(), path);
    }

    @Test
    void testInvalidCreateWithNullFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Folder(nullFile);
        });
    }

    @Test
    void testInvalidCreateWithNullElement() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Folder(nullElement, "");
        });
    }

    @Test
    void testInvalidCreateWithNullTag() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Folder(element, nullPath);
        });
    }

    @Test
    void testInvalidCreateWithElement() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Folder(slideShowElement, XMLConstants.FOLDER);
        });
    }

    @Test
    void testCompareToNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            folder.compareTo(nullFolder);
        });
    }

}
