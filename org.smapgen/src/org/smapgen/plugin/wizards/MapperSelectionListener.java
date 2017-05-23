package org.smapgen.plugin.wizards;

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

/**
 * @author Alberto Fuentes Gómez
 *
 */
class MapperSelectionListener implements SelectionListener{

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
     */
    public MapperSelectionListener(ProgressBar progressBar, boolean automaticMode, ICompilationUnit javaClass, MapperWizardHelper support,
            Combo rootPkg, Text source, Text target) {
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
     * @param automaticMode the automaticMode to set
     */
    public void setAutomaticMode(boolean automaticMode) {
        this.automaticMode = automaticMode;
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent arg0) {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetSelected(final SelectionEvent e) {
        try {
            final IPreferenceStore prop = Activator.getDefault().getPreferenceStore();
            final String input = prop.getString(PreferenceConstants.INPUT_SUFFIX);
            final String output = prop.getString(PreferenceConstants.OUTPUT_SUFFIX);
            
            progressBar.setMaximum(100);
            progressBar.setMinimum(1);
            Rectangle bound = progressBar.getBounds();
            bound.width=200;
            bound.height=20;
            progressBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            progressBar.setState(SWT.NONE);
            if (automaticMode) {
                HashMap<String, String> localSourcesClasses = new HashMap<String, String>();
                Map<String, String> localTargetClasses = new HashMap<String, String>();
                getSourcesFromClass(javaClass, localSourcesClasses, localTargetClasses);
                Map<String, String> localSourcesFromFields = new HashMap<String, String>();
                Map<String, String> localTargetFromFields = new HashMap<String, String>();
                getSourcesFromFields(javaClass, localSourcesFromFields, localTargetFromFields);
                
                int totalSize = localSourcesClasses.keySet().size()+localTargetClasses.keySet().size();
                float factor=Double.valueOf(100f/totalSize).floatValue();

                int count = 0;
                progressBar.setSelection(1);
                for (String nameSource : localSourcesClasses.keySet()) {
                    if (localTargetFromFields.containsKey(nameSource) && !localSourcesClasses.get(nameSource)
                            .equals(localTargetFromFields.get(nameSource))) {
                        support.generate(rootPkg.getText(), localSourcesClasses.get(nameSource),
                                localTargetFromFields.get(nameSource), input, output, javaClass,Boolean.TRUE);
                        progressBar.setSelection(Double.valueOf(++count*factor).intValue());
                   }
                }
                progressBar.setSelection(50);
                for (String nameSource : localTargetClasses.keySet()) {
                    if (localSourcesFromFields.containsKey(nameSource) && !localTargetClasses.get(nameSource)
                            .equals(localSourcesFromFields.get(nameSource))) {
                        support.generate(rootPkg.getText(), localSourcesFromFields.get(nameSource),
                                localTargetClasses.get(nameSource), input, output, javaClass,Boolean.TRUE);
                        progressBar.setSelection(Double.valueOf(++count*factor).intValue());
                    }
                }
            } else {
                progressBar.setSelection(1);
                support.generate(rootPkg.getText(), source.getText(), target.getText(), input, output, javaClass,Boolean.valueOf(javaClass!=null));
            }
            progressBar.setSelection(100);

        } catch (final Throwable e1) {
            e1.printStackTrace();
            progressBar.setSelection(100);
            progressBar.setState(SWT.ERROR);
        }
    }

    /**
     * @param target
     * @return
     * @throws JavaModelException
     */
    private String getImport(String target) throws JavaModelException {
        for (IImportDeclaration imported : javaClass.getImports()) {
            if (imported.getElementName().endsWith(target)) {
                return imported.getElementName();
            }
        }
        // try java.lang
        Class<?> clazz;
        try {
            clazz = Class.forName("java.lang."+target);
            return clazz.getCanonicalName();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @param javaClass
     * @param localSourcesFromFields
     * @param localTargetFromFields
     * @throws JavaModelException
     */
    private void getSourcesFromFields(ICompilationUnit javaClass, Map<String, String> localSourcesFromFields,
            Map<String, String> localTargetFromFields) throws JavaModelException {
        for (IType type : javaClass.getAllTypes()) {
            for (IField field : type.getFields()) {
                if(isAutowired(field)){
                    String signature = field.getTypeSignature();
                    String elemtype = Signature.getSignatureQualifier(signature).length() > 0
                            ? Signature.getSignatureQualifier(signature) + Constants.SupportMapper_dot
                                    + Signature.getSignatureSimpleName(signature)
                            : getImport(Signature.getSignatureSimpleName(signature));
                    try {
                        Class<?> classField = support.getSimpleCl().loadClassByName(elemtype);
                        if (isWSClient(classField)) {
                            classField = getWSCLass(classField);
                        }
                        if (null != classField) {
                            for (Method met : classField.getMethods()) {
                                for (Class<?> param : met.getParameterTypes()) {
                                    if(javaClass.getSource().contains(param.getCanonicalName())) {
                                        localTargetFromFields.put(param.getSimpleName(), param.getCanonicalName());
                                    }
                                }
                                Class<?> ret = met.getReturnType();
                                if(javaClass.getSource().contains(ret.getCanonicalName())) {
                                    localSourcesFromFields.put(ret.getSimpleName(), ret.getCanonicalName());
                                }
                            }
                        }
                    } catch (Throwable e) {
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
    private Class<?> getWSCLass(Class<?> classField) {
        for (Method met : classField.getMethods()) {
            Class<?> ret = met.getReturnType();
            for (Annotation anot : ret.getAnnotations()) {
                if ("javax.jws.WebService".equals(anot.annotationType().getCanonicalName())
                        || "org.springframework.stereotype.Service".equals(anot.annotationType().getCanonicalName())) {
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
    private boolean isAutowired(IField field) {
        return field.getAnnotation("Autowired").exists();
    }

    /**
     * @param classField
     * @return
     */
    private boolean isWSClient(Class<?> classField) {
        for (Annotation anot : classField.getAnnotations()) {
            return "javax.xml.ws.WebServiceClient".equals(anot.annotationType().getCanonicalName());
        }
        return false;
    }

    /**
     * @param javaClass
     * @param localSourcesClasses
     * @param localTargetClasses
     * @throws JavaModelException
     * @throws Throwable
     */
    void getSourcesFromClass(ICompilationUnit javaClass, HashMap<String, String> localSourcesClasses,
            Map<String, String> localTargetClasses) throws JavaModelException, Throwable {
        for (IType type : javaClass.getAllTypes()) {
            for (IMethod method : type.getMethods()) {
                if (method.getNumberOfParameters() > 0 && Flags.isPublic(method.getFlags())) {
                    String target = Signature.getReturnType(method.getSignature());
                    String fulltarget = Signature.getSignatureQualifier(target).length() > 0
                            ? Signature.getSignatureQualifier(target) + Constants.SupportMapper_dot
                                    + Signature.getSignatureSimpleName(target)
                            : getImport(Signature.getSignatureSimpleName(target));
                    if (null != fulltarget) {
                        localTargetClasses.put(Signature.getSignatureSimpleName(target), fulltarget);
                    }
                    for (String source : Signature.getParameterTypes(method.getSignature())) {
                        String fullsource = Signature.getSignatureQualifier(source).length() > 0
                                ? Signature.getSignatureQualifier(source) + Constants.SupportMapper_dot
                                        + Signature.getSignatureSimpleName(source)
                                : getImport(Signature.getSignatureSimpleName(source));
                        if (null != fullsource) {
                            localSourcesClasses.put(Signature.getSignatureSimpleName(source), fullsource);
                        }
                    }
                }
            }
        }
    }
}
