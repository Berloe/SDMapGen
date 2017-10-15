package org.smapgen.config.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Alberto Fuentes
 *
 */
public class NewAction implements IObjectActionDelegate {

    /**
     * Shell shell .
     */
    private Shell shell;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(final IAction a) {
        MessageDialog.openInformation(shell, "JavaMappingGenerator", "JavaMappingGenerator");
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(final IAction a, final ISelection s) {
        /*Not implemented*/
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void setActivePart(final IAction a, final IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

}
