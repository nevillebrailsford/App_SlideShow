package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.ISlideShowApplication;

public class AddSlideShowAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private ISlideShowApplication application;

    public AddSlideShowAction(ISlideShowApplication application) {
        super("New Slide Show");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.addSlideShowAction();
    }

}
