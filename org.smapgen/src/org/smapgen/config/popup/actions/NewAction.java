package org.smapgen.config.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class NewAction implements IObjectActionDelegate {

    /**
     * Shell shell .
     */
    private Shell shell;

    /**
     * Constructor for Action1.
     */
    public NewAction() {
    }

    @Override
    public void run(final IAction a) {
        MessageDialog.openInformation(shell, "JavaMappingGenerator", "JavaMappingGenerator");
    }

    @Override
    public void selectionChanged(final IAction a, final ISelection s) {
    }

    @Override
    public void setActivePart(final IAction a, final IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

}
