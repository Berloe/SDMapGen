package org.smapgen.plugin.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.smapgen.config.preferences.PreferenceConstants;
import org.smapgen.plugin.Activator;
import org.smapgen.plugin.etc.MapperWizardHelper;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.widgets.Control;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MapperNewWizardPage extends WizardPage {
    /**
     * String KEY_PRESS .
     */
    private static String KEY_PRESS = "Ctrl+Space";

    private Boolean automaticMode = Boolean.FALSE;

    private MapperSelectionListener gen;
    private ICompilationUnit javaClass;
    /**
     * CLabel lblNewLabel .
     */
    private CLabel lblNewLabel;
    private LoadJarSelectionAdapter loadJars;

    /**
     * String[] packages .
     */
    private String[] packages;

    /**
     * String[] rootPackages .
     */
    private String[] rootPackages;

    /**
     * String[] selection .
     */
    private String[] selection;

    /**
     * SupportMapper support .
     */
    private final MapperWizardHelper support;

    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param javaclass
     * 
     * @param pageName
     */
    public MapperNewWizardPage(final String[] selection, final MapperWizardHelper suport, ICompilationUnit javacls) {
        super("wizardPage");
        setTitle(Messages.MapperNewWizardPage_2);
        this.selection = selection;
        support = suport;
        getPackageRoots();

        this.javaClass = javacls;
    }

    @Override
    public void createControl(final Composite parent) {
        final Composite container = new Composite(parent, SWT.BORDER);
        final GridLayout layout = new GridLayout();
        layout.marginTop = 5;
        layout.marginBottom = 10;
        container.setLayout(layout);
        layout.numColumns = 4;
        layout.verticalSpacing = 9;
        new Label(container, SWT.NONE);
        final Label labelCombo = new Label(container, SWT.NULL);
        labelCombo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelCombo.setText(Messages.MapperNewWizardPage_4);

        Text text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        setAutoCompletion(text, null);
        text.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(final KeyEvent arg0) {
                setAutoCompletion(text, text.getText());
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                setAutoCompletion(text, text.getText());
            }
        });

        Button button_1 = new Button(container, SWT.NONE);
        button_1.setToolTipText(Messages.MapperNewWizardPage_button_1_toolTipText);

        GridData gd_button_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_button_1.widthHint = 30;
        button_1.setLayoutData(gd_button_1);
        button_1.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/elcl16/synced.png"));
        button_1.setText(Messages.MapperNewWizardPage_button_1_text);
        final Label labelCombo2 = new Label(container, SWT.NULL);
        labelCombo2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelCombo2.setText(Messages.MapperNewWizardPage_5);
        setControl(container);

        Text text_1 = new Text(container, SWT.BORDER);
        text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        setAutoCompletion(text_1, null);
        text_1.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(final KeyEvent arg0) {
                setAutoCompletion(text_1, text_1.getText());
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                setAutoCompletion(text_1, text_1.getText());
            }
        });
        button_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String tmp = text_1.getText();
                text_1.setText(text.getText());
                text.setText(tmp);
            }
        });
        new Label(container, SWT.NONE);
        Button btnRadioButton = new Button(container, SWT.RADIO);
        btnRadioButton.setVisible(false);
        btnRadioButton.setSelection(true);
        btnRadioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                gen.setAutomaticMode(false);
                text.setEnabled(true);
                text_1.setEnabled(true);
                button_1.setEnabled(true);
            }
        });
        btnRadioButton.setSelection(true);
        btnRadioButton.setText(Messages.MapperNewWizardPage_btnRadioButton_text);

        Button btnRadioButton_1 = new Button(container, SWT.RADIO);
        btnRadioButton_1.setText(Messages.MapperNewWizardPage_btnRadioButton_1_text);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        btnRadioButton_1.setVisible(false);
        btnRadioButton_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                gen.setAutomaticMode(true);
                text.setEnabled(false);
                text.setText("");
                text_1.setEnabled(false);
                text_1.setText("");
                button_1.setEnabled(false);
            }
        });
        if (javaClass != null) {
            btnRadioButton.setVisible(true);
            btnRadioButton_1.setVisible(true);
        }
        final Label lblPackageRoot = new Label(container, SWT.NONE);
        lblPackageRoot.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblPackageRoot.setText(Messages.MapperNewWizardPage_6);
        if (javaClass != null) {
            lblPackageRoot.setVisible(false);
        }
        Combo combo = new Combo(container, SWT.NONE);

        GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_combo.widthHint = 115;
        combo.setLayoutData(gd_combo);
        combo.setItems(rootPackages);
        if (javaClass != null) {
            combo.setVisible(false);
        }
        combo.setText(getRootPackages()[0]);
        new Label(container, SWT.NONE);

        final Button button = new Button(container, SWT.PUSH);
        button.setToolTipText(Messages.MapperNewWizardPage_button_toolTipText_1);
        button.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/eview16/filenav_nav.png"));
        button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        new Label(container, SWT.NONE);

        Combo combo_1 = new Combo(container, SWT.NONE);
        combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        combo_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final IPreferenceStore prop = Activator.getDefault().getPreferenceStore();
                final String repo = prop.getString(PreferenceConstants.P_PATHCLASSREPO);
                try {
                    Map<String, File> dependencies = support
                            .getResolvedDependendencies(support.getRepoConfig(support.getProject()), repo);
                    dependencies.get(combo_1.getText());
                    support.getSimpleCl().loadlib(dependencies.get(combo_1.getText()).getPath());
                    support.getSimpleCl().initDeps(true);
                    updateDeps();
                    lblNewLabel.setText(combo_1.getText() + Messages.MapperNewWizardPage_19);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
        });
        final IPreferenceStore prop = Activator.getDefault().getPreferenceStore();
        final String repo = prop.getString(PreferenceConstants.P_PATHCLASSREPO);
        try {
            Set<String> dependencies = support
                    .getResolvedDependendencies(support.getRepoConfig(support.getProject()), repo).keySet();
            String[] items = dependencies.toArray(new String[0]);
            Arrays.sort(items);
            combo_1.setItems(items);
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
        new Label(container, SWT.NONE);

        lblNewLabel = new CLabel(container, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        loadJars = new LoadJarSelectionAdapter(support, getShell(), selection, lblNewLabel);
        button.addSelectionListener(loadJars);
        new Label(container, SWT.NONE);
        final Button btnGeneraMtodo = new Button(container, SWT.NONE);
        btnGeneraMtodo.setToolTipText(Messages.MapperNewWizardPage_btnGeneraMtodo_toolTipText_1);
        btnGeneraMtodo.setGrayed(true);
        btnGeneraMtodo
                .setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/generic_elements.gif"));
        GridData gd_btnGeneraMtodo = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
        gd_btnGeneraMtodo.widthHint = 29;
        btnGeneraMtodo.setLayoutData(gd_btnGeneraMtodo);
        btnGeneraMtodo.setText(Messages.MapperNewWizardPage_13);
        final Label label_1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gd_label_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
        gd_label_1.widthHint = 125;
        label_1.setLayoutData(gd_label_1);
        ProgressBar progressBar = new ProgressBar(container, SWT.SMOOTH);
        GridData gd_progressBar = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 4, 1);
        gd_progressBar.widthHint = 125;
        progressBar.setLayoutData(gd_progressBar);
        progressBar.setVisible(true);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        gen = new MapperSelectionListener(progressBar, automaticMode, javaClass, support, combo, text, text_1);
        new Label(container, SWT.NONE);
        container.setTabList(new Control[]{text, button_1, text_1, btnRadioButton, btnRadioButton_1, button, combo_1, btnGeneraMtodo, combo, lblNewLabel, progressBar});
        btnGeneraMtodo.addSelectionListener(gen);

        combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                getPackagesElements();
            }

            /**
             * .
             * 
             * @return void
             */
            private void getPackagesElements() {
                IPackageFragmentRoot[] roots;
                final List<String> paths = new ArrayList<String>();
                try {
                    roots = support.getProject().getPackageFragmentRoots();
                    for (final IPackageFragmentRoot root : roots)
                        if (root.getRawClasspathEntry().getEntryKind() == IClasspathEntry.CPE_SOURCE
                                && combo.getText().equals(root.getPath().toOSString())) {
                            final IJavaElement[] chs = root.getChildren();
                            for (final IJavaElement iJavaElement : chs)
                                paths.add(iJavaElement.getPath().toString().replace("/", ".")); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                } catch (final JavaModelException e) {
                    e.printStackTrace();
                }
                setPackages(paths.toArray(new String[0]));
            }
        });
    }

    /**
     * @return the packages
     */
    public String[] getPackages() {
        return packages;
    }

    /**
     * @return the rootPackages
     */
    public String[] getRootPackages() {
        return rootPackages;
    }

    /**
     * @param packages
     *            the packages to set
     */
    public void setPackages(final String[] packages) {
        this.packages = packages;
    }

    /**
     * @param rootPackages
     *            the rootPackages to set
     */
    public void setRootPackages(final String[] rootPackages) {
        this.rootPackages = rootPackages;
    }

    /**
     * @param selection
     *            the selection to set
     */
    public void setSelection(String[] selection) {
        loadJars.setSelection(selection);
    }

    /**
     * 
     * @param String
     *            value.
     * @return String[]
     */
    private String[] getClasses(final String value) {
        if (loadJars != null) {
            selection = loadJars.getSelection();
        }
        final List<String> result = new ArrayList<String>();
        if (value == null || value.trim().length() <= 0)
            result.addAll(Arrays.asList(selection));
        else
            for (final String string : selection)
                if (string.substring(string.lastIndexOf('.') + 1, string.length()).startsWith(value))
                    result.add(string);
        final String[] $ = result.toArray(new String[0]);
        Arrays.sort($);
        return $;
    }

    /**
     * .
     * 
     * @return void
     */
    private void getPackageRoots() {
        IPackageFragmentRoot[] roots;
        final List<String> rootPaths = new ArrayList<String>();
        try {
            roots = support.getProject().getPackageFragmentRoots();
            for (final IPackageFragmentRoot root : roots)
                if (root.getRawClasspathEntry().getEntryKind() == IClasspathEntry.CPE_SOURCE)
                    rootPaths.add(root.getPath().toOSString());
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        setRootPackages(rootPaths.toArray(new String[0]));
    }

    /**
     * 
     * @param Text
     *            text.
     * @param String
     *            value
     * @return void
     */
    private void setAutoCompletion(final Text t, final String value) {
        try {
            ContentProposalAdapter adapter = null;
            final String[] defaultProposals = getClasses(value);
            final SimpleContentProposalProvider scp = new SimpleContentProposalProvider(defaultProposals);
            scp.setProposals(defaultProposals);
            final org.eclipse.jface.bindings.keys.KeyStroke ks = org.eclipse.jface.bindings.keys.KeyStroke
                    .getInstance(KEY_PRESS);
            adapter = new ContentProposalAdapter(t, new TextContentAdapter(), scp, ks, null);
            adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        } catch (final Exception e) {
            e.printStackTrace();
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