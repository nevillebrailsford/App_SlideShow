package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.ISlideShowApplication;

public class AddDirectoryAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private ISlideShowApplication application;

    public AddDirectoryAction(ISlideShowApplication application) {
        super("Add Directories");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.addDirectoryAction();
    }

}
