package applications.slideshow.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.slideshow.gui.IApplication;

public class ExitApplicationAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public ExitApplicationAction(IApplication application) {
		super("Exit Application");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.exitApplicationAction();
	}

}
