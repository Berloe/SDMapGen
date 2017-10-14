package org.smapgen.plugin.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class LogDialog extends Dialog {
    private String errorMsg;
    /**
     * Create the dialog.
     * @param parentShell
     */
    public LogDialog(Shell parentShell,String errorMsg) {
        super(parentShell);
        this.errorMsg=errorMsg;
    }

    /**
     * Create contents of the dialog.
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        StyledText styledText = new StyledText(container, SWT.BORDER);
        styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        styledText.setText(errorMsg);
        return container;
    }

    /**
     * Create contents of the button bar.
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 300);
    }

}
