package applications.slideshow.model;

import application.model.ElementBuilder;
import application.model.ElementChecker;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.tree.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Directory is the class that represents a directory to be displayed in a slide
 * show. It is also used as a container to store the directories and other slide
 * shows.
 */
public class Directory implements Comparable<Directory>, TreeNode {
    private List<Directory> store;
    private Directory parent = null;
    private boolean isDirectory = false;
    private boolean isSlideShow = false;
    private String title = null;
    private File path = null;

    /**
     * This constructor creates a slide show. It can be used to store ither
     * directories or slide shows.
     * 
     * @param title - the title for the slide show.
     */
    public Directory(String title) {
        isSlideShow = true;
        store = new ArrayList<>();
        this.title = title;
        parent = null;
    }

    /**
     * This constructor creates a directory. It is used to determine which files are
     * used by the slide show application.
     * 
     * @param path - the file that represents the path to the directory.
     */
    public Directory(File path) {
        isDirectory = true;
        this.path = path;
        store = null;
        parent = null;
    }

    /**
     * This constructor is used to build a directory from the information stored on
     * backing storage.
     * 
     * @param directoryElement - the XML element.
     */
    public Directory(Element directoryElement) {
        this(directoryElement, XMLConstants.SLIDE_SHOW, XMLConstants.DIRECTORY);
    }

    /**
     * This constructor is used to build a directory from the information stored on
     * backing storage.
     * 
     * @param directoryElement - the XML element.
     * @param tagName          - used to verify that the element is for a directory.
     * @throws IllegalArgumentException - if the element is not for a directory.
     */
    public Directory(Element directoryElement, String... tagName) {
        boolean valid = false;
        String validTag = null;
        for (String tag : tagName) {
            if (ElementChecker.verifyTag(directoryElement, tag)) {
                valid = true;
                validTag = tag;
                break;
            }
        }
        if (valid) {
            switch (validTag) {
                case XMLConstants.SLIDE_SHOW -> {
                    title = directoryElement.getElementsByTagName(XMLConstants.SLIDE_SHOW_TITLE).item(0)
                            .getTextContent();
                    store = new ArrayList<>();
                    isSlideShow = true;
                }
                case XMLConstants.DIRECTORY -> {
                    path = new File(directoryElement.getElementsByTagName(XMLConstants.DIRECTORY_PATH).item(0)
                            .getTextContent());
                    store = null;
                    isDirectory = true;
                }
                default -> {
                    throw new IllegalArgumentException("Directory: unknown tag");
                }
            }
        } else {
            throw new IllegalArgumentException("Directory: folderElement does not have a valid tag");
        }

    }

    /**
     * Build an XML element to be used for backing storage for this directory.
     * 
     * @param document - a w3 document
     * @return an XML element
     */
    public Element buildElement(Document document) {
        if (isSlideShow) {
            return buildSlideShowElement(document, XMLConstants.SLIDE_SHOW);
        } else {
            return buildDirectoryElement(document, XMLConstants.DIRECTORY);
        }
    }

    /**
     * Tests whether the directory denoted by this object is a directory.
     * 
     * @return true if and only if this directory is a directory; false otherwise
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Tests whether the directory denoted by this object is a slide show.
     * 
     * @return true if and only if this directory is a slide show; false otherwise
     */
    public boolean isSlideShow() {
        return isSlideShow;
    }

    public boolean add(Directory directory) {
        if (isDirectory) {
            return false;
        }
        if (this.equals(directory)) {
            return false;
        }
        if (directory.isSlideShow()) {
            if (contains(directory)) {
                return false;
            }
        }
        if (directory.parent != null) {
            return false;
        }
        directory.parent = this;
        return store.add(directory);
    }

    public boolean remove(Directory directory) {
        if (isDirectory) {
            return false;
        }
        return store.remove(directory);
    }

    public Directory parent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public String title() {
        if (isSlideShow) {
            return title;
        }
        return null;
    }

    public File path() {
        if (isDirectory) {
            return path;
        }
        return null;
    }

    public int totalNumberOfDirectories() {
        if (isSlideShow) {
            return store.size();
        } else {
            return 0;
        }
    }

    public List<Directory> allDirectories() {
        if (isSlideShow) {
            return store.stream().sorted().collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public int numberOfSlideShows() {
        if (isSlideShow) {
            return slideShows().size();
        } else {
            return 0;
        }
    }

    public List<Directory> slideShows() {
        if (isSlideShow) {
            return store.stream().filter((d) -> d.isSlideShow()).sorted().collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public int numberOfDirectories() {
        if (isSlideShow) {
            return directories().size();
        } else {
            return 0;
        }
    }

    public List<Directory> directories() {
        if (isSlideShow) {
            return store.stream().filter((d) -> d.isDirectory()).sorted().collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public void clear() {
        if (isSlideShow) {
            store.clear();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDirectory, isSlideShow, path, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Directory other = (Directory) obj;
        return isDirectory == other.isDirectory && isSlideShow == other.isSlideShow && Objects.equals(path, other.path)
                && Objects.equals(title, other.title);
    }

    @Override
    public int compareTo(Directory other) {
        if (isSlideShow) {
            if (other.isSlideShow) {
                return title.compareTo(other.title);
            } else {
                return 1;
            }
        } else {
            if (other.isDirectory) {
                return path.getAbsolutePath().compareTo(other.path.getAbsolutePath());
            } else {
                return -1;
            }
        }
    }

    @Override
    public String toString() {
        if (isSlideShow) {
            return title;
        } else {
            return path.getAbsolutePath();
        }
    }

    /**
     * Returns a string representation of the object
     * 
     * @return a string representation of the object
     */
    public String plainString() {
        return super.toString();
    }

    private Element buildSlideShowElement(Document document, String tagName) {
        Element result = document.createElement(tagName);
        result.appendChild(ElementBuilder.build(XMLConstants.SLIDE_SHOW_TITLE, title, document));
        return result;
    }

    private Element buildDirectoryElement(Document document, String tagName) {
        Element result = document.createElement(tagName);
        result.appendChild(ElementBuilder.build(XMLConstants.DIRECTORY_PATH, path.getAbsolutePath(), document));
        return result;

    }

    private boolean contains(Directory directory) {
        boolean result = false;
        if (store.contains(directory)) {
            result = true;
        } else {
            for (Directory dir : slideShows()) {
                result = result || dir.contains(directory);
            }
        }
        return result;
    }

    // TreeNode implementation

    @Override
    public TreeNode getChildAt(int childIndex) {
        return allDirectories().get(childIndex);
    }

    @Override
    public int getChildCount() {
        return allDirectories().size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        if (allDirectories().contains(node)) {
            return allDirectories().indexOf(node);
        } else {
            return -1;
        }
    }

    @Override
    public boolean getAllowsChildren() {
        return isSlideShow;
    }

    @Override
    public boolean isLeaf() {
        return isDirectory || allDirectories().size() == 0;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return new Enumeration<TreeNode>() {
            private int position = 0;

            @Override
            public boolean hasMoreElements() {
                return position < allDirectories().size();
            }

            @Override
            public TreeNode nextElement() {
                return allDirectories().get(position++);
            }
        };
    }

}
