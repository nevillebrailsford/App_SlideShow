package applications.slideshow.gui;

import application.notification.Notification;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import application.storage.StorageNotificationType;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import applications.slideshow.model.Directory;
import applications.slideshow.storage.SlideShowManager;

public class SlideShowTreeModel implements TreeModel {

    private List<Directory> slideShows;
    private Directory root = new Directory("Slide Shows");
    private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

    NotificationListener listener = new NotificationListener() {
        @Override
        public void notify(Notification notification) {
            if (notification.notificationType() instanceof StorageNotificationType) {
                handleStorage(notification);
            }
        }
    };

    public SlideShowTreeModel() {
        slideShows = SlideShowManager.instance().slideShows();
        NotificationCentre.addListener(listener);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent.equals(root)) {
            return slideShows.get(index);
        }
        return ((Directory) parent).allDirectories().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        Directory parentDirectory = (Directory) parent;
        if (parent.equals(root)) {
            return slideShows.size();
        } else if (parentDirectory.isSlideShow()) {
            return parentDirectory.allDirectories().size();
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        Directory nodeDirectory = (Directory) node;
        if (node.equals(root)) {
            return slideShows.size() == 0;
        }
        if (nodeDirectory.isSlideShow()) {
            return nodeDirectory.allDirectories().size() == 0;
        }
        return true;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Directory parentDirectory = (Directory) parent;
        Directory childDirectory = (Directory) child;
        if (parent == null || child == null) {
            return -1;
        }
        if (parentDirectory.equals(root) && childDirectory.isSlideShow()) {
            return locateSlideShow(childDirectory);
        } else if (parentDirectory.isSlideShow()) {
            return locateSlideShow(parentDirectory, childDirectory);
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

    protected void handleStorage(Notification notification) {
        slideShows = SlideShowManager.instance().slideShows();
        fireTreeStructureChanged(root);
    }

    private void fireTreeStructureChanged(Directory oldRoot) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[] { oldRoot });
        for (TreeModelListener tml : treeModelListeners) {
            tml.treeStructureChanged(e);
        }
    }

    private int locateSlideShow(Directory child) {
        int i = 0;
        boolean found = false;
        for (; i < slideShows.size(); i++) {
            if (slideShows.get(i).equals(child)) {
                found = true;
                break;
            }
        }
        if (found) {
            return i;
        } else {
            return -1;
        }
    }

    private int locateSlideShow(Directory parent, Directory child) {
        int i = 0;
        boolean found = false;
        for (; i < parent.allDirectories().size(); i++) {
            if (parent.allDirectories().get(i).equals(child)) {
                found = true;
                break;
            }
        }
        if (found) {
            return i;
        } else {
            return -1;
        }
    }
}
