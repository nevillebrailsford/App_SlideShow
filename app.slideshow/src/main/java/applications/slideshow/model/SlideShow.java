package applications.slideshow.model;

import application.model.ElementChecker;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A class that describes a slide show that contains information about the
 * folders from which pictures will be displayed when the slideshow starts. It
 * can also include other slide shows.
 */
public class SlideShow extends Folder implements Comparable<Folder> {

    private List<Folder> folders;
    private String title;

    /**
     * General constructor with a title.
     * 
     * @param title of the slide show.
     */
    public SlideShow(String title) {
        if (title == null) {
            throw new InvalidParameterException("SlideShow: title is null");
        }
        if (title.isEmpty()) {
            throw new InvalidParameterException("SlideShow: title is empty");
        }
        folders = new ArrayList<>();
        this.title = title;
    }

    /**
     * Constructor used when loading the data at application initialisation.
     * 
     * @param slideShowElement that is an XML element describing the slide show.
     */
    public SlideShow(Element slideShowElement) {
        this(slideShowElement, XMLConstants.SLIDE_SHOW);
    }

    /**
     * Constructor used when loading the data at application initialisation.
     * 
     * @param slideShowElement that is an XML element describing the slide show.
     * @param tagName          that XML tag identifier that should have been used.
     */
    public SlideShow(Element slideShowElement, String tagName) {
        if (slideShowElement == null) {
            throw new InvalidParameterException("SlideShow: slideShowElement is null");
        }
        if (!ElementChecker.verifyTag(slideShowElement, tagName)) {
            throw new IllegalArgumentException("SlideShow: slideShowElement is not for folder");
        }
        String title = slideShowElement.getAttribute(XMLConstants.SLIDE_SHOW_TITLE);
        this.title = title;
    }

    /**
     * Construct an XML element that describes this slide show.
     * 
     * @return a w3 element.
     */
    public Element buildElement(Document document) {
        return buildElement(document, XMLConstants.SLIDE_SHOW);
    }

    /**
     * Construct an XML element that describes this slide show.
     * 
     * @param document is a w3 specified document
     * @param tagName  is the element identifier to be used to identify a slide show
     *                 element
     * @return a w3 element.
     */
    public Element buildElement(Document document, String tagName) {
        if (document == null) {
            throw new IllegalArgumentException("SlideShow: document is null");
        }
        Element result = document.createElement(tagName);
        result.setAttribute(XMLConstants.SLIDE_SHOW_TITLE, title);
        return result;
    }

    /**
     * Get the title for this slide show.
     * 
     * @return the title
     */
    public String title() {
        return title;
    }

    /**
     * get a list of all the folders and slide shows in this slide show.
     * 
     * @return list of folders and slide shows.
     */
    public List<Folder> all() {
        List<Folder> allFolders = folders.stream().sorted().collect(Collectors.toList());
        return allFolders;
    }

    /**
     * get a list of all the folders in this slide show.
     * 
     * @return list of folders.
     */
    public List<Folder> folders() {
        List<Folder> folderList = folders.stream().filter((f) -> f.isFolder()).sorted().collect(Collectors.toList());
        return folderList;
    }

    /**
     * get a list of all the slide shows in this slide show.
     * 
     * @return list of slide shows.
     */
    public List<Folder> slideShows() {
        List<Folder> slideShows = folders.stream().filter((f) -> f.isSlideShow()).sorted().collect(Collectors.toList());
        return slideShows;
    }

    /**
     * get a count of the folders in this slide show.
     * 
     * @return count of folders.
     */
    public int numberOfFolders() {
        return (int) folders.stream().filter((f) -> f.isFolder()).count();
    }

    /**
     * get a count of the slide shows in this slide show.
     * 
     * @return count of slide shows.
     */
    public int numberOfSlideShows() {
        return (int) folders.stream().filter((f) -> f.isSlideShow()).count();
    }

    /**
     * Get the number of folders contained within this slide show. This includes all
     * the folders within the contained slide shows as well.
     * 
     * @return total count of folders.
     */
    public int totalNumberOfFolders() {
        return (int) folders.stream().flatMap((f) -> f.totalFolders().stream()).count();
    }

    /**
     * Get the list of folders contained within this slide show. This includes all
     * the folders within the contained slide shows as well.
     * 
     * @return list of folders.
     */

    @Override
    public List<Folder> totalFolders() {
        return folders.stream().flatMap((f) -> f.totalFolders().stream()).sorted().collect(Collectors.toList());
    }

    /**
     * Get a list of files that the folders within this slide show represent.
     * 
     * @return list of files.
     */
    public List<File> files() {
        return folders.stream().flatMap((f) -> f.totalFolders().stream()).filter((f) -> f.isFolder()).sorted()
                .map((f) -> f.file()).collect(Collectors.toList());
    }

    /**
     * Add a new folder or slide show to this slide show.
     * 
     * @param newFolder the new folder or slide show.
     * @return whether the add succeeded. You cannot add the same slide show to a
     *         slide show.
     */
    public boolean addFolder(Folder newFolder) {
        if (newFolder == this) {
            return false;
        }
        return folders.add(newFolder);
    }

    /**
     * Remove a folder or slide show from the folder
     * 
     * @param oldFolder the old folder or slide show
     * @return whether the remove succeeded.
     */
    public boolean removeFolder(Folder oldFolder) {
        return folders.remove(oldFolder);
    }

    /**
     * Clear the slide show of any folders.
     */
    public void clear() {
        folders = new ArrayList<>();
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public boolean isSlideShow() {
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(title);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SlideShow other = (SlideShow) obj;
        return Objects.equals(title, other.title);
    }

    @Override
    public int compareTo(Folder other) {
        if (this == other) {
            return 0;
        }
        if (other == null) {
            throw new InvalidParameterException("SlideShow: other is null");
        }
        if (other.isFolder()) {
            return super.compareTo(other);
        } else {
            return title.compareTo(((SlideShow) other).title);
        }
    }

}
