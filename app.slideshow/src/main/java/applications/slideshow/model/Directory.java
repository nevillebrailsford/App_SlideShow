package applications.slideshow.model;

import application.model.ElementBuilder;
import application.model.ElementChecker;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Directory implements Comparable<Directory> {
    private List<Directory> store;
    private boolean isDirectory = false;
    private boolean isSlideShow = false;
    private String title = null;
    private File path = null;

    public Directory(String title) {
        isSlideShow = true;
        store = new ArrayList<>();
        this.title = title;
    }

    public Directory(File path) {
        isDirectory = true;
        this.path = path;
        store = null;
    }

    public Directory(Element directoryElement) {
        this(directoryElement, XMLConstants.SLIDE_SHOW, XMLConstants.DIRECTORY);
    }

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

    public Element buildElement(Document document) {
        if (isSlideShow) {
            return buildSlideShowElement(document, XMLConstants.SLIDE_SHOW);
        } else {
            return buildDirectoryElement(document, XMLConstants.DIRECTORY);
        }
    }

    public boolean isDirectory() {
        return isDirectory;
    }

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
        return store.add(directory);
    }

    public boolean remove(Directory directory) {
        if (isDirectory) {
            return false;
        }
        return store.remove(directory);
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
                return other.title.compareTo(title);
            } else {
                return 1;
            }
        } else {
            if (other.isDirectory) {
                return other.path.getAbsolutePath().compareTo(path.getAbsolutePath());
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

}
