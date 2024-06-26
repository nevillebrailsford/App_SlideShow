package applications.slideshow;

import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColorProvider;
import application.base.app.gui.GUIConstants;
import application.base.app.gui.PreferencesDialog;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.inifile.IniFile;
import application.storage.LoadData;
import application.storage.StoreDetails;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import applications.slideshow.actions.ExitApplicationAction;
import applications.slideshow.dialog.SlideShowPreferences;
import applications.slideshow.gui.IApplication;
import applications.slideshow.gui.SlideShowMenu;
import applications.slideshow.model.Directory;
import applications.slideshow.storage.SlideShowLoad;
import applications.slideshow.storage.SlideShowManager;

public class SlideShowApplication extends ApplicationBaseForGUI implements IApplication {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowApplication.class.getName();

    private static Logger LOGGER = null;

    private static final String HOME = "C:/Users/nevil/OneDrive/Pictures/";
    private JFrame parent;
    private JButton exit;

    public SlideShowApplication() {
    }

    @Override
    public void configureStoreDetails() {
        dataLoader = new SlideShowLoad();
        storeDetails = new StoreDetails(dataLoader, Constants.MODEL, Constants.SLIDE_SHOW_FILE);
    }

    @Override
    public ApplicationDefinition createApplicationDefinition(Parameters parameters) {
        ApplicationDefinition definition = new ApplicationDefinition(parameters.getNamed().get("name")) {

            @Override
            public boolean hasModelFile() {
                return false;
            }

            @Override
            public Optional<Color> bottomColor() {
                String bottom = IniFile.value(GUIConstants.BOTTOM_COLOR);
                if (bottom.isEmpty() || bottom.equals("default")) {
                    bottom = "teal";
                    IniFile.store(GUIConstants.BOTTOM_COLOR, bottom);
                }
                Color bottomColor = ColorProvider.get(bottom);
                return Optional.ofNullable(bottomColor);
            }

            @Override
            public Optional<Color> topColor() {
                String top = IniFile.value(GUIConstants.TOP_COLOR);
                if (top.isEmpty() || top.equals("default")) {
                    top = "lightskyblue";
                    IniFile.store(GUIConstants.TOP_COLOR, top);
                }
                Color topColor = ColorProvider.get(top);
                return Optional.ofNullable(topColor);
            }
        };
        return definition;
    }

    @Override
    public void start(JFrame parent) {
        LOGGER = ApplicationConfiguration.logger();
        LOGGER.entering(CLASS_NAME, "start");
        this.parent = parent;
        System.out.println(
                "Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is starting");
        parent.setLayout(new BorderLayout());
        SlideShowMenu sm = new SlideShowMenu(this);
        parent.setJMenuBar(sm);
        JSplitPane mainPanel = new JSplitPane();
        add(mainPanel, BorderLayout.CENTER);
        JTree tree = new JTree(SlideShowManager.instance());
        JScrollPane treeView = new JScrollPane();
        treeView.setPreferredSize(new Dimension(300, 500));
        treeView.setViewportView(tree);
        mainPanel.setLeftComponent(treeView);
        JPanel buttonPanel = new BottomColoredPanel();
        buttonPanel.setLayout(new FlowLayout());
        exit = new JButton(new ExitApplicationAction(this));
        buttonPanel.add(exit);
        add(buttonPanel, BorderLayout.PAGE_END);
        pack();
        LOGGER.exiting(CLASS_NAME, "start");
    }

    private JFileChooser createFileChooserDialog() {
        JFileChooser fc = new JFileChooser(HOME);
        fc.setDialogTitle("Select which directories you'd like to view.");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(true);
        fc.addActionListener((e) -> {
            if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                File[] files = fc.getSelectedFiles();
                new SlideShowDisplay(files);
            } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                shutdown();
            }
        });
        UIDefaults defaults = new UIDefaults();
        defaults.put("FileChooser.background", ApplicationConfiguration.applicationDefinition().topColor());
        defaults.put("FileChooser.textForeground", Color.black);

        fc.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
        fc.putClientProperty("Nimbus.Overrides", defaults);
        return fc;
    }

    @Override
    public void terminate() {
        LOGGER.entering(CLASS_NAME, "terminate");
        System.out.println(
                "Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is stopping");
        LOGGER.exiting(CLASS_NAME, "terminate");
    }

    @Override
    public boolean loadExistingModel(LoadData arg0, String arg1, String arg2) {
        return super.loadExistingModel(arg0, arg1, arg2);
    }

    public static void main(String[] args) {
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        launch(args);
    }

    @Override
    public void preferencesAction() {
        LOGGER.entering(CLASS_NAME, "preferencesAction");
        PreferencesDialog dialog = new SlideShowPreferences(parent);
        dialog.setVisible(true);
        dialog.dispose();
        LOGGER.exiting(CLASS_NAME, "preferencesAction");
    }

    @Override
    public void newSlideShowAction() {
        LOGGER.entering(HOME, "newSlideShowAction");
        String title = JOptionPane.showInputDialog(this, "Please provide a title");
        if (title != null && !title.isEmpty()) {
            try {
                Directory newShow = new Directory(title);
                SlideShowManager.instance().addSlideShow(newShow);
            } catch (Throwable e) {
                System.out.println(e);
            }
        }
        LOGGER.exiting(HOME, "newSlideShowAction");
    }

    @Override
    public void exitApplicationAction() {
        LOGGER.entering(CLASS_NAME, "exitApplicationAction");
        try {
            shutdown();
        } catch (Exception e) {
        }
        LOGGER.exiting(CLASS_NAME, "exitApplicationAction");
    }

}
