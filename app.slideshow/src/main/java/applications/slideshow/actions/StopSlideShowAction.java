package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.ISlideShowApplication;

public class StopSlideShowAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private ISlideShowApplication application;

    public StopSlideShowAction(ISlideShowApplication application) {
        super("Stop slide show");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.stopSlideShowAction();
    }

}
