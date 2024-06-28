package applications.slideshow.gui;

import application.definition.ApplicationConfiguration;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import applications.slideshow.actions.ExitApplicationAction;
import applications.slideshow.actions.NewSlideShowAction;
import applications.slideshow.actions.PreferencesAction;

public class SlideShowMenu extends JMenuBar {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowMenu.class.getName();
    private static Logger LOGGER = ApplicationConfiguration.logger();

    @SuppressWarnings("unused")
    private IApplication application;

    private JMenu fileMenu = new JMenu("File");
    private JMenuItem preferences;
    private JMenuItem newSlideShow;
    private JMenuItem exit;

    public SlideShowMenu(IApplication application) {
        LOGGER.entering(CLASS_NAME, "init");
        this.application = application;
        add(fileMenu);
        newSlideShow = new JMenuItem(new NewSlideShowAction(application));
        preferences = new JMenuItem(new PreferencesAction(application));
        exit = new JMenuItem(new ExitApplicationAction(application));
        fileMenu.add(newSlideShow);
        fileMenu.addSeparator();
        fileMenu.add(preferences);
        fileMenu.add(exit);
        LOGGER.exiting(CLASS_NAME, "init");
    }

}
