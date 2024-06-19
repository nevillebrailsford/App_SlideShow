package applications.slideshow.dialog;

import application.base.app.gui.PreferencesDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The preferences dialog for the slide show application. This class adds some
 * slide show specific options to the base PreferencesDialog class.
 */
public class SlideShowPreferences extends PreferencesDialog {
    private static final long serialVersionUID = 1L;

    private JLabel lbl_displayFor;
    private JTextField displayFor;
    private JLabel lbl_seconds;

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

        contentPanel.add(lbl_displayFor);
        contentPanel.add(displayFor);
        contentPanel.add(lbl_seconds);
    }

    /**
     * Call back method from base PreferencesDialog, to save any additional
     * preferences.
     */
    @Override
    public void saveAdditionalPreferences() {
        System.out.println("c");
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

}
