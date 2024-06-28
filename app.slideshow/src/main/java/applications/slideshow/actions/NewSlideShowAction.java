package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.gui.IApplication;

public class NewSlideShowAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private IApplication application;

    public NewSlideShowAction(IApplication application) {
        super("New Slide Show");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.newSlideShowAction();
    }

}
