package applications.slideshow.gui;

import application.definition.ApplicationConfiguration;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import applications.slideshow.actions.SlideShowActionFactory;

public class SlideShowPopup extends JPopupMenu {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowPopup.class.getName();
    private static Logger LOGGER = ApplicationConfiguration.logger();

    public SlideShowPopup() {
        LOGGER.entering(CLASS_NAME, "init");
        add(new JMenuItem(SlideShowActionFactory.instance().addDirectoryAction()));
        add(new JMenuItem(SlideShowActionFactory.instance().addSlideShowToAction()));
        addSeparator();
        add(new JMenuItem(SlideShowActionFactory.instance().startSlideShowAction()));
        add(new JMenuItem(SlideShowActionFactory.instance().pauseSlideShowAction()));
        add(new JMenuItem(SlideShowActionFactory.instance().resumeSlideShowAction()));
        add(new JMenuItem(SlideShowActionFactory.instance().stopSlideShowAction()));
        addSeparator();
        JMenuItem copyItem = new JMenuItem(SlideShowActionFactory.instance().copyAction());
        add(copyItem);
        JMenuItem pasteItem = new JMenuItem(SlideShowActionFactory.instance().pasteAction());
        add(pasteItem);
        JMenuItem deleteItem = new JMenuItem(SlideShowActionFactory.instance().deleteAction());
        add(deleteItem);
        LOGGER.exiting(CLASS_NAME, "init");
    }
}
