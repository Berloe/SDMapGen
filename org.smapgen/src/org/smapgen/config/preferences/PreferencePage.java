package org.smapgen.config.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.smapgen.plugin.Activator;
import org.smapgen.plugin.etc.MapperWizardHelper;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main
 * plug-in class. That way, preferences can be accessed directly via the preference store.
 */

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    /**
     * SupportMapper suport .
     */
    private final MapperWizardHelper suport;

    /**
     * .
     * 
     * @return void
     */
    public PreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(""); //$NON-NLS-1$
        suport = new MapperWizardHelper();
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
     * types of preferences. Each field editor knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors() {
        addField(new DirectoryFieldEditor(PreferenceConstants.P_PATHCLASSREPO, Messages.PreferencePage_0,
                getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.INPUT_SUFFIX, Messages.PreferencePage_2, -1,
                StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.OUTPUT_SUFFIX, Messages.PreferencePage_3, -1,
                StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
        addField(new org.eclipse.jface.preference.ComboFieldEditor(PreferenceConstants.FORMATTING, Messages.PreferencePage_4,getFormattingDefaults(), getFieldEditorParent()));
    }

    /**
     * 
     * @param IWorkbench
     *            workbench.
     * @return void
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(final IWorkbench w) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    /**
     * .
     * 
     * @return boolean
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {

        final String texto = ((Text) getFieldEditorParent().getTabList()[0]).getText();
        final IPreferenceStore store = getPreferenceStore();
        store.setValue(PreferenceConstants.P_PATHCLASSREPO, texto);
        final String textoIn = ((Text) getFieldEditorParent().getTabList()[3]).getText();
        store.setValue(PreferenceConstants.INPUT_SUFFIX, textoIn);
        final String textoOut = ((Text) getFieldEditorParent().getTabList()[4]).getText();
        store.setValue(PreferenceConstants.OUTPUT_SUFFIX, textoOut);
        setPreferenceStore(store);
        if (texto != null && texto.trim().length() > 0)
            try {
                suport.genClassMapFile(texto);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        return super.performOk();
    }

    private String[][] getFormattingDefaults() {
        String[][] out = new String[4][2];
        out[0][0]="Current Active";
        out[0][1]="Current";
        out[1][0]="Eclipse Default";
        out[1][1]="EclipseDefault";
        out[2][0]="Eclipse21 Default";
        out[2][1]="Eclipse21";
        out[3][0]="Java Conventions Default";
        out[3][1]="JavaConventions";
        return out;
    }
}