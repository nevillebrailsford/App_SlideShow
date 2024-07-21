package applications.slideshow.gui;

import application.definition.ApplicationConfiguration;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import applications.slideshow.SlideShowApplication;
import applications.slideshow.actions.ActionFactory;

public class SlideShowTree extends JTree {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowTree.class.getName();
    private static Logger LOGGER = ApplicationConfiguration.logger();

    private ActionFactory actionFactory;

    public SlideShowTree(TreeModel manager, IApplication application) {
        super(manager);
        LOGGER.entering(CLASS_NAME, "init");
        actionFactory = ActionFactory.instance(application);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setComponentPopupMenu(createPopupMenu(application));
        ImageIcon showIcon = createImageIcon("slide-show-64.png");
        ImageIcon dirIcon = createImageIcon("directory-64.png");
        setCellRenderer(new TreeCellRenderer(showIcon, dirIcon));
        setKeyBindings(this);
        LOGGER.exiting(CLASS_NAME, "init");
    }

    private void setKeyBindings(JTree tree) {
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK),
                actionFactory.undoAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK),
                actionFactory.redoAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),
                actionFactory.copyAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),
                actionFactory.pasteAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK),
                actionFactory.deleteAction());
    }

    private JPopupMenu createPopupMenu(IApplication application) {
        SlideShowPopup menu = new SlideShowPopup(application);
        return menu;
    }

    private static ImageIcon createImageIcon(String path) {
        LOGGER.entering(CLASS_NAME, "createImageIcon", path);
        java.net.URL imgURL = SlideShowApplication.class.getResource(path);
        if (imgURL != null) {
            ImageIcon result = new ImageIcon(imgURL);
            Image image = result.getImage();
            Image newImage = image.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            result = new ImageIcon(newImage);
            LOGGER.exiting(CLASS_NAME, "createImageIcon");
            return result;
        } else {
            LOGGER.warning("Couldn't find path " + path);
            LOGGER.exiting(CLASS_NAME, "createImageIcon");
            return null;
        }
    }

}
