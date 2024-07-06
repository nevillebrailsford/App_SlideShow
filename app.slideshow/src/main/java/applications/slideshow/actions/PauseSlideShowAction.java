package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.gui.IApplication;

public class PauseSlideShowAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private IApplication application;

    public PauseSlideShowAction(IApplication application) {
        super("Pause slide show");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.pauseSlideShowAction();
    }

}
