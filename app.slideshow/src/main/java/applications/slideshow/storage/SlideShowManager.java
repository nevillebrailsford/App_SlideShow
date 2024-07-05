package applications.slideshow.storage;

import application.audit.AuditService;
import application.definition.ApplicationConfiguration;
import application.storage.Storage;
import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import applications.slideshow.model.Directory;

/**
 * The class that keeps track of all the directories in all the slide shows.
 * <p>
 * This class acts as a TreeModel as well, so is used by the tree in the main
 * application window as its model.
 */
public class SlideShowManager implements TreeModel {
    private static final String CLASS_NAME = SlideShowManager.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    private static SlideShowManager instance = null;

    private Directory root = new Directory("Slide shows");
    private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

    private SlideShowStore slideShowStore;
    private File modelDirectory;
    private File dataFile;
    private Storage storage;

    /**
     * The method by which the other classes in the application gain access to the
     * SlideShowManager object.
     * 
     * @return the SlideShowManager
     */
    public synchronized static SlideShowManager instance() {
        LOGGER.entering(CLASS_NAME, "instance");
        if (instance == null) {
            instance = new SlideShowManager();
        }
        LOGGER.exiting(CLASS_NAME, "instance", instance);
        return instance;
    }

    /**
     * Private constructor to prevent being created (almost) anywhere else
     */
    private SlideShowManager() {
        LOGGER.entering(CLASS_NAME, "cinit");
        slideShowStore = new SlideShowStore();
        modelDirectory = obtainModelDirectory();
        dataFile = new File(modelDirectory, ModelConstants.SLIDE_SHOW_FILE);
        slideShowStore.setFileName(dataFile.getAbsolutePath());
        storage = new Storage();
        LOGGER.exiting(CLASS_NAME, "cinit");
    }

    public void clear() {
        LOGGER.entering(CLASS_NAME, "clear");
        root.clear();
        updateStorage();
        LOGGER.exiting(CLASS_NAME, "clear");
    }

    public List<Directory> allDirectories() {
        LOGGER.entering(CLASS_NAME, "allDirectories");
        List<Directory> copyList = root.allDirectories().stream().sorted().collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "allDirectories", copyList);
        return copyList;
    }

    public List<Directory> slideShows() {
        LOGGER.entering(CLASS_NAME, "slideShows");
        List<Directory> copyList = root.slideShows().stream().sorted().collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "slideShows", copyList);
        return copyList;
    }

    public List<Directory> directories() {
        LOGGER.entering(CLASS_NAME, "directories");
        List<Directory> copyList = root.directories().stream().sorted().collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "directories", copyList);
        return copyList;
    }

    public Directory slideShow(String title) {
        LOGGER.entering(CLASS_NAME, "slideShow", title);
        Directory result = null;
        List<Directory> shows = slideShows();
        for (Directory show : shows) {
            if (show.title().equals(title)) {
                result = show;
                break;
            }
        }
        LOGGER.exiting(CLASS_NAME, "slideShow", result);
        return result;
    }

    public List<Directory> directories(Directory show) {
        LOGGER.entering(CLASS_NAME, "directories", show);
        List<Directory> copyList = show.directories().stream().sorted().collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "directories", copyList);
        return copyList;
    }

    public List<Directory> slideShows(Directory show) {
        LOGGER.entering(CLASS_NAME, "slideShows", show);
        List<Directory> copyList = show.slideShows().stream().sorted().collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "slideShows", copyList);
        return copyList;
    }

    public void addSlideShow(Directory newShow) {
        LOGGER.entering(CLASS_NAME, "addSlideShow", newShow);
        if (newShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "addSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw exc;
        }
        if (newShow.isDirectory()) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow is a directory");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "addSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw exc;
        }
        if (slideShows().contains(newShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow already exists");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "addSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw exc;
        }
        try {
            synchronized (root) {
                boolean success = root.add(newShow);
                if (success) {
                    AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.SlideShow,
                            newShow.title());
                    updateStorage();
                } else {
                    updateFailed(new Exception("Unable to add " + newShow));
                }
            }
        } catch (Exception e) {
            updateFailed(e);
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "addSlideShow", e);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "addSlideShow");
    }

    public void removeSlideShow(Directory oldShow) {
        LOGGER.entering(CLASS_NAME, "removeSlideShow", oldShow);
        if (oldShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: oldShow is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "removeSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShow");
            throw exc;
        }
        if (!slideShows().contains(oldShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: oldShow not known");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "removeSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShow");
            throw exc;
        }
        try {
            synchronized (root) {
                boolean success = root.remove(oldShow);
                if (success) {
                    AuditService.writeAuditInformation(SlideShowAuditType.Removed, SlideShowAuditObject.SlideShow,
                            oldShow.title());
                    updateStorage();
                } else {
                    updateFailed(new Exception("Unable to remove " + oldShow));
                }
            }
        } catch (Exception e) {
            updateFailed(e);
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "removeSlideShow", e);
            LOGGER.exiting(CLASS_NAME, "removeSlideShow");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "removeSlideShow");
    }

    /**
     * Add a slide show to an existing slide show. This will take a copy newShow
     * before being added to the tree. Because of this the new copy will be returned
     * from this method.
     * 
     * @param pathToSlideShow - the TreePath that represents the slide show to which
     *                        newShow will be added
     * @param newShow         - the slide show to be added.
     * @return a copy of the slide show, updated with the correct parent
     *         information.
     */
    public Directory addSlideShowTo(TreePath pathToSlideShow, Directory newShow) {
        LOGGER.entering(CLASS_NAME, "addSlideShowTo", new Object[] { pathToSlideShow, newShow });
        Directory result = null;
        if (pathToSlideShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        if (newShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        try {
            synchronized (root) {
                Directory slideShow = findSlideShow(pathToSlideShow);
                result = newShow.copy();
                boolean success = slideShow.add(result);
                result.setParent(slideShow);
                if (success) {
                    AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.SlideShow,
                            newShow.title() + " added to " + slideShow.title());
                    updateStorage();
                } else {
                    updateFailed(new Exception("Unable to add " + newShow));
                }
            }
        } catch (Exception e) {
            updateFailed(e);
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "addFolder", e);
            LOGGER.exiting(CLASS_NAME, "addFolder");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "addSlideShowTo", result);
        return result;
    }

    public void removeSlideShowFrom(TreePath pathToSlideShow, Directory oldShow) {
        LOGGER.entering(CLASS_NAME, "removeSlideShowFrom", new Object[] { pathToSlideShow, oldShow });
        if (pathToSlideShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "removeSlideShowFrom", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
            throw exc;
        }
        if (oldShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: oldShow is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "removeSlideShowFrom", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
            throw exc;
        }
        try {
            synchronized (root) {
                Directory slideShow = findSlideShow(pathToSlideShow);
                boolean success = slideShow.remove(oldShow);
                if (success) {
                    AuditService.writeAuditInformation(SlideShowAuditType.Removed, SlideShowAuditObject.SlideShow,
                            oldShow.title() + " remove from " + slideShow.title());
                    updateStorage();
                } else {
                    updateFailed(new Exception("Unable to remove " + oldShow));
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "removeSlideShowFrom", e);
            LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
            updateFailed(e);
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
    }

    public void addDirectory(TreePath pathToSlideShow, Directory newDirectory) {
        LOGGER.entering(CLASS_NAME, "addDirectory", new Object[] { pathToSlideShow, newDirectory });
        if (pathToSlideShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: slideShow is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "addDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "addDirectory");
            throw exc;
        }
        if (newDirectory == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newDirectory is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "addDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "addDirectory");
            throw exc;
        }
        try {
            synchronized (root) {
                Directory show = findSlideShow(pathToSlideShow);
                boolean success = show.add(newDirectory);
                if (success) {
                    AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.Folder,
                            newDirectory.path().getAbsolutePath());
                    updateStorage();
                } else {
                    updateFailed(new Exception("Unable to add " + newDirectory));
                }
            }
        } catch (Exception e) {
            updateFailed(e);
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "addDirectory", e);
            LOGGER.exiting(CLASS_NAME, "addDirectory");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "addDirectory");
    }

    public void removeDirectory(TreePath pathToSlideShow, Directory oldDirectory) {
        LOGGER.entering(CLASS_NAME, "removeDirectory", new Object[] { pathToSlideShow, oldDirectory });
        if (pathToSlideShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: slideShow is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "removeDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "removeDirectory");
            throw exc;
        }
        if (oldDirectory == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: oldDirectory is null");
            updateFailed(exc);
            LOGGER.throwing(CLASS_NAME, "removeDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "removeDirectory");
            throw exc;
        }
        try {
            synchronized (root) {
                Directory slideShow = findSlideShow(pathToSlideShow);
                boolean success = slideShow.remove(oldDirectory);
                if (success) {
                    AuditService.writeAuditInformation(SlideShowAuditType.Removed, SlideShowAuditObject.Folder,
                            oldDirectory.path().getAbsolutePath());
                    updateStorage();
                } else {
                    updateFailed(new Exception("Unable to remove " + oldDirectory));
                }
            }
        } catch (Exception e) {
            updateFailed(e);
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "removeDirectory", e);
            LOGGER.exiting(CLASS_NAME, "removeDirectory");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "removeDirectory");
    }

    public TreePath treePath(Directory directory) {
        LOGGER.entering(CLASS_NAME, "treePath", directory);
        Vector<Directory> path = new Vector<>();
        while (directory != null) {
            path.insertElementAt(directory, 0);
            directory = directory.parent();
        }
        Directory[] paths = new Directory[path.size()];
        for (int i = 0; i < path.size(); i++) {
            paths[i] = path.get(i);
        }
        TreePath result = new TreePath(paths);
        LOGGER.exiting(CLASS_NAME, "treePath", result);
        return result;
    }

    public File[] files(TreePath ss) {
        LOGGER.entering(CLASS_NAME, "files", ss);
        File[] files = null;
        Directory theSlideShow = (Directory) ss.getLastPathComponent();
        if (theSlideShow.isSlideShow()) {
            List<Directory> directories = theSlideShow.directories();
            for (Directory d : theSlideShow.slideShows()) {
                directories.addAll(files(d));
            }
            files = new File[directories.size()];
            for (int i = 0; i < directories.size(); i++) {
                files[i] = directories.get(i).path();
            }
        } else {
            files = new File[1];
            files[0] = theSlideShow.path();
        }
        LOGGER.exiting(CLASS_NAME, "files", files);
        return files;
    }

    public List<Directory> files(Directory ss) {
        LOGGER.entering(CLASS_NAME, "files", ss);
        List<Directory> directories = ss.directories();
        for (Directory d : ss.slideShows()) {
            directories.addAll(files(d));
        }
        LOGGER.exiting(CLASS_NAME, "files", directories);
        return directories;
    }

    public Directory root() {
        LOGGER.entering(CLASS_NAME, "root");
        Directory rootDirectory = (Directory) getRoot();
        LOGGER.exiting(CLASS_NAME, "root", rootDirectory);
        return rootDirectory;
    }

    private Directory findSlideShow(TreePath pathToSlideShow) {
        LOGGER.entering(CLASS_NAME, "findSlideShow", pathToSlideShow);
        Directory result = (Directory) pathToSlideShow.getLastPathComponent();
        LOGGER.exiting(CLASS_NAME, "findSlideShow", result);
        return result;
    }

    private void updateStorage() {
        LOGGER.entering(CLASS_NAME, "updateStorage");
        storage.storeData(slideShowStore);
        fireTreeStructureChanged(root);
        LOGGER.exiting(CLASS_NAME, "updateStorage");
    }

    private void updateFailed(Exception e) {
        LOGGER.entering(CLASS_NAME, "updateFailed", e);
        slideShowStore.signalStoreFailed(e);
        LOGGER.exiting(CLASS_NAME, "updateFailed");
    }

    private File obtainModelDirectory() {
        LOGGER.entering(CLASS_NAME, "obtainModelDirectory");
        File rootDirectory = ApplicationConfiguration.rootDirectory();
        File applicationDirectory = new File(rootDirectory,
                ApplicationConfiguration.applicationDefinition().applicationName());
        File modelDirectory = new File(applicationDirectory, ModelConstants.MODEL);
        if (!modelDirectory.exists()) {
            LOGGER.fine("Model directory " + modelDirectory.getAbsolutePath() + " does not exist");
            if (!modelDirectory.mkdirs()) {
                LOGGER.warning("Unable to create model directory");
                modelDirectory = null;
            } else {
                LOGGER.fine("Created model directory " + modelDirectory.getAbsolutePath());
            }
        } else {
            LOGGER.fine("Model directory " + modelDirectory.getAbsolutePath() + " does exist");
        }
        LOGGER.exiting(CLASS_NAME, "obtainModelDirectory", modelDirectory);
        return modelDirectory;
    }

    @Override
    public Object getRoot() {
        LOGGER.entering(CLASS_NAME, "getRoot");
        LOGGER.exiting(CLASS_NAME, "getRoot", root);
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        LOGGER.entering(CLASS_NAME, "getChild", new Object[] { parent, index });
        Directory node = (Directory) parent;
        Directory result = (Directory) node.getChildAt(index);
        LOGGER.exiting(CLASS_NAME, "getChild", result);
        return result;
    }

    @Override
    public int getChildCount(Object parent) {
        LOGGER.entering(CLASS_NAME, "getChildCount", parent);
        Directory directory = (Directory) parent;
        int result = directory.getChildCount();
        LOGGER.exiting(CLASS_NAME, "getChildCount", result);
        return result;
    }

    @Override
    public boolean isLeaf(Object node) {
        LOGGER.entering(CLASS_NAME, "isLeaf", node);
        Directory directory = (Directory) node;
        boolean result = directory.isLeaf();
        LOGGER.exiting(CLASS_NAME, "isLeaf", result);
        return result;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        LOGGER.entering(CLASS_NAME, "getIndexOfChild", new Object[] { parent, child });
        Directory parentDirectory = (Directory) parent;
        Directory childDirectory = (Directory) child;
        int result = parentDirectory.getIndex(childDirectory);
        LOGGER.exiting(CLASS_NAME, "getIndexOfChild", result);
        return result;

    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

    private void fireTreeStructureChanged(Directory oldRoot) {
        LOGGER.entering(CLASS_NAME, "fireTreeStructureChanged", oldRoot);
        TreeModelEvent e = new TreeModelEvent(this, new Object[] { oldRoot });
        for (TreeModelListener tml : treeModelListeners) {
            tml.treeStructureChanged(e);
        }
        LOGGER.exiting(CLASS_NAME, "fireTreeStructureChanged");
    }

}
