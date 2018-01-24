package org.smapgen.plugin.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.smapgen.plugin.wizards.MapperNewWizard;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ActionLauncher extends ActionDelegate implements IObjectActionDelegate {
    /**
     * ISelection selection .
     */
    private ISelection selection;
    /**
     * IWorkbench workbench .
     */
    private IWorkbench workbench;

    /**
     *
     * @param IAction
     *            arg0.
     * @return void
     */
    @Override
    public void run(final IAction arg0) {
        // Create the wizard
        final MapperNewWizard wizard = new MapperNewWizard();
        wizard.init(workbench, (IStructuredSelection) selection);

        // Create the wizard dialog
        final WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
        // Open the wizard dialog
        dialog.open();
    }

    @Override
    public void selectionChanged(final IAction a, final ISelection s) {
        selection = s;
    }

    @Override
    public void setActivePart(final IAction a, final IWorkbenchPart targetPart) {
        targetPart.getSite().getShell();
        workbench = targetPart.getSite().getWorkbenchWindow().getWorkbench();
    }
}
