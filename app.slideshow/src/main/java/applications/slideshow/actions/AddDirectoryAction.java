package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.gui.IApplication;

public class AddDirectoryAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private IApplication application;

    public AddDirectoryAction(IApplication application) {
        super("Add Directory");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.addDirectoryAction();
    }

}
