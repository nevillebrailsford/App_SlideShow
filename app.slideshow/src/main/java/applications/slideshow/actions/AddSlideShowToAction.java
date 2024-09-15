package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.ISlideShowApplication;

public class AddSlideShowToAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private ISlideShowApplication application;

    public AddSlideShowToAction(ISlideShowApplication application) {
        super("Add Slide Show To");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.addSlideShowToAction();
    }

}
