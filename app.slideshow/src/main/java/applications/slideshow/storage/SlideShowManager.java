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

public class SlideShowManager implements TreeModel {
    private static final String CLASS_NAME = SlideShowManager.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    private static SlideShowManager instance = null;

    private Directory root = new Directory("Slide shows");
    private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

    public synchronized static SlideShowManager instance() {
        LOGGER.entering(CLASS_NAME, "instance");
        if (instance == null) {
            instance = new SlideShowManager();
        }
        LOGGER.exiting(CLASS_NAME, "instance", instance);
        return instance;
    }

    private SlideShowManager() {
        LOGGER.entering(CLASS_NAME, "cinit");
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
        LOGGER.entering(title, "slideShow", title);
        Directory result = null;
        List<Directory> shows = slideShows();
        for (Directory show : shows) {
            if (show.title().equals(title)) {
                result = show;
                break;
            }
        }
        LOGGER.exiting(title, "slideShow", result);
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
            LOGGER.throwing(CLASS_NAME, "addSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw exc;
        }
        if (newShow.isDirectory()) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow is a directory");
            LOGGER.throwing(CLASS_NAME, "addSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw exc;
        }
        if (slideShows().contains(newShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow already exists");
            LOGGER.throwing(CLASS_NAME, "addSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw exc;
        }
        try {
            synchronized (root) {
                if (root.add(newShow)) {
                    AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.SlideShow,
                            newShow.title());
                    updateStorage();
                }
            }
        } catch (Exception e) {
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
            LOGGER.throwing(CLASS_NAME, "removeSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShow");
            throw exc;
        }
        if (!slideShows().contains(oldShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: oldShow not known");
            LOGGER.throwing(CLASS_NAME, "removeSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShow");
            throw exc;
        }
        try {
            synchronized (root) {
                if (root.remove(oldShow)) {
                    AuditService.writeAuditInformation(SlideShowAuditType.Removed, SlideShowAuditObject.SlideShow,
                            oldShow.title());
                    updateStorage();
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "removeSlideShow", e);
            LOGGER.exiting(CLASS_NAME, "removeSlideShow");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "removeSlideShow");
    }

    public void addSlideShowTo(Directory show, Directory newShow) {
        LOGGER.entering(CLASS_NAME, "addSlideShowTo", new Object[] { show, newShow });
        if (show == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show is null");
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        if (newShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow is null");
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        if (!slideShows().contains(show)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show not known");
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        try {
            synchronized (root) {
                Directory slideShow = findSlideShow(show);
                if (slideShow != null) {
                    if (slideShow.add(newShow)) {
                        AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.SlideShow,
                                newShow.title() + " added to " + show.title());
                        updateStorage();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "addFolder", e);
            LOGGER.exiting(CLASS_NAME, "addFolder");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
    }

    public void removeSlideShowFrom(Directory show, Directory oldShow) {
        LOGGER.entering(CLASS_NAME, "removeSlideShowFrom", new Object[] { show, oldShow });
        if (show == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show is null");
            LOGGER.throwing(CLASS_NAME, "removeSlideShowFrom", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
            throw exc;
        }
        if (oldShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: oldShow is null");
            LOGGER.throwing(CLASS_NAME, "removeSlideShowFrom", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
            throw exc;
        }
        if (show.equals(oldShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show equals oldShow");
            LOGGER.throwing(CLASS_NAME, "removeSlideShowFrom", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
            throw exc;
        }
        if (!slideShows().contains(show)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show not known");
            LOGGER.throwing(CLASS_NAME, "removeSlideShowFrom", exc);
            LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
            throw exc;
        }
        try {
            synchronized (root) {
                Directory slideShow = findSlideShow(show);
                if (slideShow != null) {
                    if (slideShow.remove(oldShow)) {
                        AuditService.writeAuditInformation(SlideShowAuditType.Removed, SlideShowAuditObject.SlideShow,
                                oldShow.title() + " remove from " + show.title());
                        updateStorage();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "removeSlideShowFrom", e);
            LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "removeSlideShowFrom");
    }

    public void addDirectory(Directory slideShow, Directory newDirectory) {
        LOGGER.entering(CLASS_NAME, "addDirectory", new Object[] { slideShow, newDirectory });
        if (slideShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: slideShow is null");
            LOGGER.throwing(CLASS_NAME, "addDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "addDirectory");
            throw exc;
        }
        if (newDirectory == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newDirectory is null");
            LOGGER.throwing(CLASS_NAME, "addDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "addDirectory");
            throw exc;
        }
        if (!slideShows().contains(slideShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: slideShow not known");
            LOGGER.throwing(CLASS_NAME, "addDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "addDirectory");
            throw exc;
        }
        try {
            synchronized (root) {
                Directory show = findSlideShow(slideShow);
                if (show != null) {
                    if (slideShow.add(newDirectory)) {
                        AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.Folder,
                                newDirectory.path().getAbsolutePath());
                        updateStorage();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "addDirectory", e);
            LOGGER.exiting(CLASS_NAME, "addDirectory");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "addDirectory");
    }

    public void removeDirectory(Directory slideShow, Directory oldDirectory) {
        LOGGER.entering(CLASS_NAME, "removeDirectory", new Object[] { slideShow, oldDirectory });
        if (slideShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: slideShow is null");
            LOGGER.throwing(CLASS_NAME, "removeDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "removeDirectory");
            throw exc;
        }
        if (oldDirectory == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: oldDirectory is null");
            LOGGER.throwing(CLASS_NAME, "removeDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "removeDirectory");
            throw exc;
        }
        if (!slideShows().contains(slideShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: slideShow not known");
            LOGGER.throwing(CLASS_NAME, "removeDirectory", exc);
            LOGGER.exiting(CLASS_NAME, "removeDirectory");
            throw exc;
        }
        try {
            synchronized (root) {
                Directory show = findSlideShow(slideShow);
                if (show != null) {
                    if (slideShow.remove(oldDirectory)) {
                        AuditService.writeAuditInformation(SlideShowAuditType.Removed, SlideShowAuditObject.Folder,
                                oldDirectory.path().getAbsolutePath());
                        updateStorage();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "removeDirectory", e);
            LOGGER.exiting(CLASS_NAME, "removeDirectory");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "removeDirectory");
    }

    private Directory findSlideShow(Directory show) {
        LOGGER.entering(CLASS_NAME, "findSlideShow", show);
        List<Directory> shows = slideShows().stream().filter((s) -> s.equals(show)).collect(Collectors.toList());
        Directory result = shows.size() >= 1 ? shows.get(0) : null;
        LOGGER.exiting(CLASS_NAME, "findSlideShow", result);
        return result;
    }

    private void updateStorage() {
        LOGGER.entering(CLASS_NAME, "updateStorage");
        SlideShowStore slideShowStore = new SlideShowStore();
        File modelDirectory = obtainModelDirectory();
        File dataFile = new File(modelDirectory, ModelConstants.SLIDE_SHOW_FILE);
        slideShowStore.setFileName(dataFile.getAbsolutePath());
        Storage storage = new Storage();
        storage.storeData(slideShowStore);
        fireTreeStructureChanged(root);
        LOGGER.exiting(CLASS_NAME, "updateStorage");
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
