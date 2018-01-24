package org.smapgen.plugin.wizards;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.smapgen.config.preferences.PreferenceConstants;
import org.smapgen.plugin.Activator;
import org.smapgen.plugin.etc.MapperWizardHelper;

/**
 * @author Alberto Fuentes Gómez
 *
 */
class LoadJarSelectionAdapter extends SelectionAdapter {
    /**
     * CLabel lblNewLabel .
     */
    private final CLabel lblNewLabel;
    /**
     * String[] selection .
     */
    private String[] selection;
    private final Shell shell;

    private final MapperWizardHelper support;

    /**
     * @param support
     * @param shell
     * @param selection
     * @param lblNewLabel
     */
    public LoadJarSelectionAdapter(final MapperWizardHelper support, final Shell shell, final String[] selection, final CLabel lblNewLabel) {
        super();
        this.support = support;
        this.shell = shell;
        setSelection(selection);
        this.lblNewLabel = lblNewLabel;
    }

    /**
     * @return the selection
     */
    public String[] getSelection() {
        return selection;
    }

    /**
     * @param selection
     *            the selection to set
     */
    public void setSelection(final String[] selection) {
        this.selection = selection;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetSelected(final SelectionEvent e) {
        final FileDialog dialog = new FileDialog(shell, SWT.PUSH);
        dialog.setText(Messages.MapperNewWizardPage_15);
        final IPreferenceStore prop = Activator.getDefault().getPreferenceStore();
        final String path = prop.getString(PreferenceConstants.LASTUSEDPATH);
        dialog.setFilterPath(path);
        dialog.setFilterExtensions(new String[] { "*.jar", "*.class" });
        final String selected = dialog.open();
        final String newPath = selected.substring(0, selected.lastIndexOf(System.getProperty("file.separator")));
        prop.setValue(PreferenceConstants.LASTUSEDPATH, newPath);
        org.eclipse.jface.preference.JFacePreferences.setPreferenceStore(prop);
        try {
            support.getSimpleCl().loadlib(selected);
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }

        lblNewLabel.setText(selected + Messages.MapperNewWizardPage_19);
        try {
            updateDeps();
        } catch (final Throwable e1) {
            e1.printStackTrace();
        }
    }

    /**
     * .
     * 
     * @return void
     * @throws Throwable
     */
    private void updateDeps() throws Throwable {
        final String[] selectionData = support.getSimpleCl().getLoadedClassNames();
        for (int i = 0; i < selectionData.length; ++i) {
            final String string = selectionData[i].replace('/', '.');
            selectionData[i] = string;
        }
        setSelection(selectionData);
    }

}
