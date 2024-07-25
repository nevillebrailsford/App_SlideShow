package applications.slideshow.dialog;

import application.base.app.gui.PreferencesDialog;
import application.inifile.IniFile;
import java.util.StringTokenizer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import applications.slideshow.Constants;

/**
 * The preferences dialog for the slide show application. This class adds some
 * slide show specific options to the base PreferencesDialog class.
 */
public class SlideShowPreferences extends PreferencesDialog {
    private static final long serialVersionUID = 1L;

    private JLabel lbl_displayFor;
    private JTextField displayFor;
    private JLabel lbl_seconds;

    private JComboBox<String> size;
    private JLabel lbl_size;
    private JLabel lbl_empty;

    /**
     * Standard constructor for a dialog.
     * 
     * @param parent the frame owning this dialog.
     */
    public SlideShowPreferences(JFrame parent) {
        super(parent);
    }

    /**
     * Call back method from base PreferencesDialog, to add new action listeners
     */
    @Override
    public void additionalActionListeners() {
        displayFor.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateValue(displayFor.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                validateValue(displayFor.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateValue(displayFor.getText());
            }
        });
    }

    /**
     * Call back method from base PreferencesDialog, to add new options.
     * 
     * @param contentPanel the panel to add objects to.
     */
    @Override
    public void additionalGUIItems(JPanel contentPanel) {
        lbl_displayFor = new JLabel("Display picture for");
        displayFor = new JTextField();
        displayFor.setColumns(10);
        lbl_seconds = new JLabel("seconds");

        lbl_size = new JLabel("Size");
        size = new JComboBox<>();
        lbl_empty = new JLabel(" ");

        contentPanel.add(lbl_displayFor);
        contentPanel.add(displayFor);
        contentPanel.add(lbl_seconds);

        contentPanel.add(lbl_size);
        contentPanel.add(size);
        contentPanel.add(lbl_empty);

        loadContents();
    }

    /**
     * Call back method from base PreferencesDialog, to save any additional
     * preferences.
     */
    @Override
    public void saveAdditionalPreferences() {
        saveDisplayContents();
        saveScreenSizeContents();
    }

    private void validateValue(String text) {
        if (text.isEmpty()) {
            validInput();
        } else {
            if (text.matches("\\d+")) {
                validInput();
            } else {
                invalidInput();
            }
        }
    }

    private void loadContents() {
        loadDisplayContents();
        loadScreenSizeContents();
    }

    private void saveDisplayContents() {
        IniFile.store(Constants.DISPLAY_SECONDS, displayFor.getText());
    }

    private void loadDisplayContents() {
        String displaySeconds = IniFile.value(Constants.DISPLAY_SECONDS);
        if (displaySeconds == null || displaySeconds.isEmpty()) {
            displaySeconds = Constants.DEFAULT_SCREENSECONDS;
        }
        displayFor.setText(displaySeconds);
    }

    private void saveScreenSizeContents() {
        StringTokenizer st = new StringTokenizer(size.getItemAt(size.getSelectedIndex()), "x");
        String width = st.nextToken();
        String height = st.nextToken();
        IniFile.store(Constants.SCREEN_WIDTH, width);
        IniFile.store(Constants.SCREEN_HEIGHT, height);
    }

    private void loadScreenSizeContents() {
        size.addItem("640x480");
        size.addItem("800x600");
        size.addItem("1024x768");
        size.addItem("1280x1024");
        size.addItem("1366x768");
        size.addItem("1920x1080");
        String width = IniFile.value(Constants.SCREEN_WIDTH);
        String height = IniFile.value(Constants.SCREEN_HEIGHT);
        if (width == null || width.isEmpty()) {
            width = Constants.DEFAULT_SCREEN_WIDTH;
        }
        if (height == null || height.isEmpty()) {
            height = Constants.DEFAULT_SCREEN_HEIGHT;
        }
        String chosenSize = width + "x" + height;
        size.setSelectedItem(chosenSize);
    }
}
