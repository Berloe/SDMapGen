package org.smapgen.plugin.wizards;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.smapgen.config.preferences.PreferenceConstants;
import org.smapgen.plugin.Activator;
import org.smapgen.plugin.etc.Constants;
import org.smapgen.plugin.etc.MapperWizardHelper;
import org.smapgen.sdm.config.SDataObjMapperConfig;

/**
 * @author Alberto Fuentes Gómez
 *
 */
class MapperSelectionListener implements SelectionListener {

    protected boolean automaticMode;
    protected ICompilationUnit javaClass;
    protected ProgressBar progressBar;

    protected Combo rootPkg;

    protected Text source;
    protected MapperWizardHelper support;
    protected Text target;

    /**
     * @param progressBar
     * @param automaticMode
     * @param javaClass
     * @param support
     * @param rootPkg
     * @param source
     * @param target
     * @param styledText
     */
    public MapperSelectionListener(final ProgressBar progressBar, final boolean automaticMode, final ICompilationUnit javaClass, final MapperWizardHelper support, final Combo rootPkg, final Text source, final Text target) {
        super();
        this.progressBar = progressBar;
        this.automaticMode = automaticMode;
        this.javaClass = javaClass;
        this.support = support;
        this.rootPkg = rootPkg;
        this.source = source;
        this.target = target;
    }

    /**
     * @return the automaticMode
     */
    public boolean isAutomaticMode() {
        return automaticMode;
    }

    /**
     * @param automaticMode
     *            the automaticMode to set
     */
    public void setAutomaticMode(final boolean automaticMode) {
        this.automaticMode = automaticMode;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetDefaultSelected(final SelectionEvent arg0) {
        /* Not implemented */
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetSelected(final SelectionEvent e) {
        try {
            final IPreferenceStore prop = Activator.getDefault().getPreferenceStore();
            final String input = prop.getString(PreferenceConstants.INPUT_SUFFIX);
            final String output = prop.getString(PreferenceConstants.OUTPUT_SUFFIX);
            final Integer threshold = Integer.valueOf(prop.getInt(PreferenceConstants.COMPAT_THRESHOLD));
            final SDataObjMapperConfig mapConf = new SDataObjMapperConfig(input, output, threshold);

            progressBar.setMaximum(100);
            progressBar.setMinimum(1);
            final Rectangle bound = progressBar.getBounds();
            bound.width = 200;
            bound.height = 20;
            progressBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            progressBar.setState(SWT.NONE);
            if (automaticMode) {
                final HashMap<String, String> localSourcesClasses = new HashMap<>();
                final Map<String, String> localTargetClasses = new HashMap<>();
                getSourcesFromClass(javaClass, localSourcesClasses, localTargetClasses);
                final Map<String, String> localSourcesFromFields = new HashMap<>();
                final Map<String, String> localTargetFromFields = new HashMap<>();
                getSourcesFromFields(javaClass, localSourcesFromFields, localTargetFromFields);

                final int totalSize = localSourcesClasses.keySet().size() + localTargetClasses.keySet().size();
                final float factor = 100f / totalSize;

                int count = 0;
                progressBar.setSelection(1);
                for (final String nameSource : localSourcesClasses.keySet()) {
                    if (localTargetFromFields.containsKey(nameSource) && !localSourcesClasses.get(nameSource).equals(localTargetFromFields.get(nameSource))) {
                        support.generate(rootPkg.getText(), localSourcesClasses.get(nameSource), localTargetFromFields.get(nameSource), mapConf, javaClass, Boolean.TRUE);
                        progressBar.setSelection((int) (++count * factor));
                    }
                }
                progressBar.setSelection(50);
                for (final String nameSource : localTargetClasses.keySet()) {
                    if (localSourcesFromFields.containsKey(nameSource) && !localTargetClasses.get(nameSource).equals(localSourcesFromFields.get(nameSource))) {
                        support.generate(rootPkg.getText(), localSourcesFromFields.get(nameSource), localTargetClasses.get(nameSource), mapConf, javaClass, Boolean.TRUE);
                        progressBar.setSelection((int) (++count * factor));
                    }
                }
            } else {
                progressBar.setSelection(1);
                support.generate(rootPkg.getText(), source.getText(), target.getText(), mapConf, javaClass, Boolean.valueOf(javaClass != null));
            }
            progressBar.setSelection(100);

        } catch (final Throwable e1) {
            e1.printStackTrace();
            progressBar.setSelection(100);
            progressBar.setState(SWT.ERROR);
            final StringWriter err = new StringWriter();
            // Pring error Log
            e1.printStackTrace(new PrintWriter(err));
            // Show error log into a dialog
            final LogDialog dialog = new LogDialog(progressBar.getParent().getShell(), err.toString());
            dialog.open();
        }
    }

    /**
     * @param target
     * @return
     * @throws JavaModelException
     */
    private String getImport(final String target) throws JavaModelException {
        for (final IImportDeclaration imported : javaClass.getImports()) {
            if (imported.getElementName().endsWith(target)) {
                return imported.getElementName();
            }
        }
        // try java.lang
        Class<?> clazz;
        try {
            clazz = Class.forName("java.lang." + target);
            return clazz.getCanonicalName();
        } catch (final ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @param javaClass
     * @param localSourcesClasses
     * @param localTargetClasses
     * @throws JavaModelException
     * @throws Throwable
     */
    private void getSourcesFromClass(final ICompilationUnit javaClass, final HashMap<String, String> localSourcesClasses, final Map<String, String> localTargetClasses) throws JavaModelException, Throwable {
        for (final IType type : javaClass.getAllTypes()) {
            for (final IMethod method : type.getMethods()) {
                if (method.getNumberOfParameters() > 0 && Flags.isPublic(method.getFlags())) {
                    final String target = Signature.getReturnType(method.getSignature());
                    final String fulltarget = Signature.getSignatureQualifier(target).length() > 0 ? Signature.getSignatureQualifier(target) + Constants.SupportMapper_dot + Signature.getSignatureSimpleName(target) : getImport(Signature.getSignatureSimpleName(target));
                    if (null != fulltarget) {
                        localTargetClasses.put(Signature.getSignatureSimpleName(target), fulltarget);
                    }
                    for (final String source : Signature.getParameterTypes(method.getSignature())) {
                        final String fullsource = Signature.getSignatureQualifier(source).length() > 0 ? Signature.getSignatureQualifier(source) + Constants.SupportMapper_dot + Signature.getSignatureSimpleName(source) : getImport(Signature.getSignatureSimpleName(source));
                        if (null != fullsource) {
                            localSourcesClasses.put(Signature.getSignatureSimpleName(source), fullsource);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param javaClass
     * @param localSourcesFromFields
     * @param localTargetFromFields
     * @throws JavaModelException
     */
    private void getSourcesFromFields(final ICompilationUnit javaClass, final Map<String, String> localSourcesFromFields, final Map<String, String> localTargetFromFields) throws JavaModelException {
        for (final IType type : javaClass.getAllTypes()) {
            for (final IField field : type.getFields()) {
                if (isAutowired(field)) {
                    final String signature = field.getTypeSignature();
                    final String elemtype = Signature.getSignatureQualifier(signature).length() > 0 ? Signature.getSignatureQualifier(signature) + Constants.SupportMapper_dot + Signature.getSignatureSimpleName(signature) : getImport(Signature.getSignatureSimpleName(signature));
                    try {
                        Class<?> classField = support.getSimpleCl().loadClassByName(elemtype);
                        if (isWSClient(classField)) {
                            classField = getWSCLass(classField);
                        }
                        if (null != classField) {
                            for (final Method met : classField.getMethods()) {
                                for (final Class<?> param : met.getParameterTypes()) {
                                    if (javaClass.getSource().contains(param.getCanonicalName())) {
                                        localTargetFromFields.put(param.getSimpleName(), param.getCanonicalName());
                                    }
                                }
                                final Class<?> ret = met.getReturnType();
                                if (javaClass.getSource().contains(ret.getCanonicalName())) {
                                    localSourcesFromFields.put(ret.getSimpleName(), ret.getCanonicalName());
                                }
                            }
                        }
                    } catch (final Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @param classField
     * @return
     */
    private Class<?> getWSCLass(final Class<?> classField) {
        for (final Method met : classField.getMethods()) {
            final Class<?> ret = met.getReturnType();
            for (final Annotation anot : ret.getAnnotations()) {
                if ("javax.jws.WebService".equals(anot.annotationType().getCanonicalName()) || "org.springframework.stereotype.Service".equals(anot.annotationType().getCanonicalName())) {
                    return ret;
                }
            }
        }
        return null;
    }

    /**
     * @param field
     * @return
     */
    private boolean isAutowired(final IField field) {
        return field.getAnnotation("Autowired").exists();
    }

    /**
     * @param classField
     * @return
     */
    private boolean isWSClient(final Class<?> classField) {
        for (final Annotation anot : classField.getAnnotations()) {
            if ("javax.xml.ws.WebServiceClient".equals(anot.annotationType().getCanonicalName())) {
                return true;
            }
        }
        return false;
    }

}
