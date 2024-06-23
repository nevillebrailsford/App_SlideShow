package applications.slideshow.model;

import application.model.ElementBuilder;
import application.model.ElementChecker;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class that holds a path reference.
 */
public class Folder implements Comparable<Folder> {

    private String path;

    /**
     * Standard constructor with no path specified.
     */
    public Folder() {
    }

    /**
     * General constructor with a file to contain the path.
     * 
     * @param file that points to the folder containing photos.
     */
    public Folder(File file) {
        if (file == null) {
            throw new InvalidParameterException("Folder: file is null");
        }
        this.path = file.getAbsolutePath();
    }

    /**
     * Constructor used when loading the data at application initialisation.
     * 
     * @param folderElement that is an XML element describing the folder.
     */
    public Folder(Element folderElement) {
        this(folderElement, XMLConstants.FOLDER);
    }

    /**
     * Constructor used when loading the data at application initialisation.
     * 
     * @param folderElement that is an XML element describing the folder.
     * @param tagName       that XML tag identifier that should have been used.
     */
    public Folder(Element folderElement, String tagName) {
        if (folderElement == null) {
            throw new InvalidParameterException("Folder: folderElement is null");
        }
        if (!ElementChecker.verifyTag(folderElement, tagName)) {
            throw new IllegalArgumentException("Folder: folderElement is not for folder");
        }
        String path = folderElement.getElementsByTagName(XMLConstants.FOLDER_PATH).item(0).getTextContent();
        this.path = path;
    }

    /**
     * Construct an XML element that describes this folder.
     * 
     * @param document is a w3 specified document
     * @return a w3 element.
     */
    public Element buildElement(Document document) {
        return buildElement(document, XMLConstants.FOLDER);
    }

    /**
     * Construct an XML element that describes this folder.
     * 
     * @param document is a w3 specified document
     * @param tagName  is the element identifier to be used to identify a folder
     *                 element
     * @return a w3 element.
     */
    public Element buildElement(Document document, String tagName) {
        if (document == null) {
            throw new IllegalArgumentException("Folder: document is null");
        }
        Element result = document.createElement(tagName);
        result.appendChild(ElementBuilder.build(XMLConstants.FOLDER_PATH, path, document));
        return result;
    }

    /**
     * Get the path for this folder.
     * 
     * @return a String
     */
    public String path() {
        return path;
    }

    /**
     * Is this object a folder.
     * 
     * @return true if so
     */
    public boolean isFolder() {
        return true;
    }

    /**
     * Is this a slide show.
     * 
     * @return true if so
     */
    public boolean isSlideShow() {
        return false;
    }

    /**
     * Get the list of folders contained within this parents slide show. This
     * includes all the folders within the contained slide shows as well.
     * 
     * @return list of folders.
     */

    public List<Folder> totalFolders() {
        List<Folder> f = new ArrayList<>();
        f.add(this);
        return f;
    }

    /**
     * Get the folder as a file.
     * 
     * @return a file
     */
    public File file() {
        if (this.isFolder()) {
            return new File(path);
        } else {
            return null;
        }
    }

    /**
     * Method that will return a Folder as a SlideShow.
     * 
     * @return this as a SlideShow if it is really a SlideShow object.
     * @throws IllegalArgumentException if it is a Folder object.
     */
    public SlideShow asSlideShow() {
        if (isSlideShow()) {
            return (SlideShow) this;
        } else {
            throw new IllegalArgumentException("Folder: this is not a slide show");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Folder other = (Folder) obj;
        return Objects.equals(path, other.path);
    }

    @Override
    public int compareTo(Folder other) {
        if (this == other) {
            return 0;
        }
        if (other == null) {
            throw new InvalidParameterException("Folder: other is null");
        }
        if (getClass() != other.getClass()) {
            return -1;
        }
        return path.compareTo(other.path);
    }

}
