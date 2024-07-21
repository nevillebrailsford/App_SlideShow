package applications.slideshow.gui;

import application.definition.ApplicationConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import applications.slideshow.actions.ActionFactory;

public class SlideShowPopup extends JPopupMenu {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowPopup.class.getName();
    private static Logger LOGGER = ApplicationConfiguration.logger();

    private ActionFactory actionFactory;

    public SlideShowPopup(IApplication application) {
        LOGGER.entering(CLASS_NAME, "init");
        actionFactory = ActionFactory.instance(application);
        add(new JMenuItem(actionFactory.addDirectoryAction()));
        add(new JMenuItem(actionFactory.addSlideShowToAction()));
        addSeparator();
        add(new JMenuItem(actionFactory.startSlideShowAction()));
        add(new JMenuItem(actionFactory.pauseSlideShowAction()));
        add(new JMenuItem(actionFactory.resumeSlideShowAction()));
        add(new JMenuItem(actionFactory.stopSlideShowAction()));
        addSeparator();
        JMenuItem copyItem = new JMenuItem(actionFactory.copyAction());
        add(copyItem);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        JMenuItem pasteItem = new JMenuItem(actionFactory.pasteAction());
        add(pasteItem);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        JMenuItem deleteItem = new JMenuItem(actionFactory.deleteAction());
        add(deleteItem);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        LOGGER.exiting(CLASS_NAME, "init");
    }
}
