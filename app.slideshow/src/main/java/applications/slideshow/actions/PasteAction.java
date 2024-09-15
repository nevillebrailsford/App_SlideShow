package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.ISlideShowApplication;

public class PasteAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private ISlideShowApplication application;

    public PasteAction(ISlideShowApplication application) {
        super("Paste");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.pasteAction();
    }

}
