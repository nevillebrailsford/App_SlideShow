package applications.slideshow.gui;

import application.definition.ApplicationConfiguration;
import java.awt.Image;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

public class SlideShowTree extends JTree {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowTree.class.getName();
    private static Logger LOGGER = ApplicationConfiguration.logger();

    public SlideShowTree(TreeModel manager) {
        super(manager);
        LOGGER.entering(CLASS_NAME, "init");
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setComponentPopupMenu(createPopupMenu());
        ImageIcon showIcon = createImageIcon("slide-show-64.png");
        ImageIcon dirIcon = createImageIcon("directory-64.png");
        setCellRenderer(new TreeCellRenderer(showIcon, dirIcon));
        LOGGER.exiting(CLASS_NAME, "init");
    }

    private JPopupMenu createPopupMenu() {
        SlideShowPopup menu = new SlideShowPopup();
        return menu;
    }

    private static ImageIcon createImageIcon(String path) {
        LOGGER.entering(CLASS_NAME, "createImageIcon", path);
        java.net.URL imgURL = SlideShowTree.class.getResource(path);
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
