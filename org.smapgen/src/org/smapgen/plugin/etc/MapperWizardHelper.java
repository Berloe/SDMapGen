package org.smapgen.plugin.etc;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.smapgen.config.preferences.PreferenceConstants;
import org.smapgen.dconf.Dconf;
import org.smapgen.plugin.Activator;
import org.smapgen.scl.SimpleClassLoader;
import org.smapgen.scl.repo.IRepoProvider;
import org.smapgen.sdm.SimpleDataObjMapper;
import org.smapgen.sdm.config.SDataObjMapperConfig;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MapperWizardHelper {
    /**
     * String classdef .
     */
    private static final String classdef = Constants.SupportMapper_classHeader
            + System.getProperty(Constants.SupportMapper_newLine)
            + System.getProperty(Constants.SupportMapper_newLine) + Constants.SupportMapper_endTag;
    /**
     * IJavaProject project .
     */
    private IJavaProject project;
    /**
     * SimpleClassLoader simpleCl .
     */
    private SimpleClassLoader simpleCl;

    /**
     * .
     * 
     * @return void
     */
    public MapperWizardHelper() {
        simpleCl = new SimpleClassLoader();
    }


    /**
     * @param output
     * @param input
     * @param depth
	 * @param isMainPrivate 
     * @param rootPKG,String
     *            source,String target
     * @return
     * @throws Throwable
     */
    public boolean generate(final String rootPKG, final String sourceParam, final String targetParam, final String input, final String output,ICompilationUnit cu, Boolean isMainPrivate) throws Throwable {
        final String source = sourceParam;
        final String target = targetParam;
        StringBuffer[] fileStB = getFileString(source, target, simpleCl, input, output,cu,rootPKG, isMainPrivate);
        return fileStB == null || genfile(fileStB, rootPKG,cu);
    }
    public StringBuffer[] getFileString(final String sourceName, final String targetName,
            final SimpleClassLoader l, final String input, final String output, ICompilationUnit cu, String rootPKG, Boolean isMainPrivate)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, Throwable {
        if (sourceName != null && !"".equals(sourceName.trim())) {
            final Class<?> source = l.loadClassByName(sourceName.trim());
            if (targetName != null && !"".equals(targetName.trim())) {
                final Class<?> target = l.loadClassByName(targetName.trim());
                SDataObjMapperConfig conf = new SDataObjMapperConfig(input, output);
                final SimpleDataObjMapper sm = new SimpleDataObjMapper(conf);
                loadFunctionsFromCu(cu,rootPKG,sm);
                final StringBuffer[] s = sm.mappers( source, target, isMainPrivate);
                if (s.length > 0) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * @return the project
     */
    public IJavaProject getProject() {
        return project;
    }

	public File getRepoConfig (IJavaProject prj) throws Throwable{
        return new File(prj.getProject().getFile(Dconf.getInstance().getRepoImpl().getConfigFileName()).getLocationURI());
    }

    public Map<String, File> getResolvedDependendencies (File conf,String repo) throws Throwable{
        IRepoProvider repository = Dconf.getInstance().getRepoNewInstance(new File(repo).toPath()); 
        Map<String, URI> filesURI = repository.getResolveDependenciesURL(repository.getDependencies(conf), new File(repo).toPath());
        Map<String, File> result = new HashMap<String, File>();
        for (Entry<String, URI> uri : filesURI.entrySet()) {
            result.put(uri.getKey(),new File(uri.getValue()));
        }
        return result;
    }

    public Map<String, File> getResolvedTransitiveDependendencies (File conf,String repo) throws Throwable{
        IRepoProvider repository = Dconf.getInstance().getRepoNewInstance(new File(repo).toPath()); 
        Map<String, URI> filesURI = repository.getResolveDependenciesURL(repository.getTransitiveDependencies(conf), new File(repo).toPath());
        Map<String, File> result = new HashMap<String, File>();
        for (Entry<String, URI> uri : filesURI.entrySet()) {
            result.put(uri.getKey(),new File(uri.getValue()));
        }
        return result;
    }

    /**
     * 
     * @param String
     *            value.
     * @return Map<String,URL>
     */
    public Class<?>[] getserializedHash(final String value) {
        FileInputStream fileIn;
        try {
            fileIn = new FileInputStream(value + "/ClassCache.ser");
        } catch (final FileNotFoundException e) {
            return null;
        }
        ObjectInputStream in;
        try {
            List<Class<?>> hashList = new ArrayList<Class<?>>();
            in = new ObjectInputStream(fileIn);
            try {
                while (true) {
                    try {
                        final Class<?> hash = (Class<?>) in.readObject();
                        if (hash != null) {
                            hashList.add(hash);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (EOFException e1) {
                e1.printStackTrace();
            }
            in.close();
            fileIn.close();
            return hashList.toArray(new Class<?>[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return the simpleCl
     */
    public SimpleClassLoader getSimpleCl() {
        return simpleCl;
    }

    /**
     * @param p
     *            the project to set
     */
    public void setProject(final IJavaProject p) {
        this.project = p;
    }

    /**
     * @param l
     *            the simpleCl to set
     */
    public void setSimpleCl(final SimpleClassLoader l) {
        this.simpleCl = l;
    }

    /**
     * 
     * @param cu2 
     * @param StringBuffer[]
     *            filedatas.
     * @param String
     *            rootPKG
     * @return boolean
     */
    private boolean genfile(final StringBuffer[] filedatas, final String rootPKG, ICompilationUnit javaClass) {
        try {
        			ICompilationUnit cu;
        			if(javaClass!=null){
                    	cu = javaClass;
        			}else{
        				cu = getCompilationUnitFromPKG(rootPKG);
        			}
        			IType type;
        			if(javaClass!=null){
        			   type = cu.getAllTypes()[0];
        			}else{
        			   type = cu.getType(Constants.SupportMapper_defaultFile);
        			}
                    for (final StringBuffer method : filedatas) {
                        type.createMethod(method.toString(), null, true, null);
                    }
                    Map<?, ?> setting = null;
                    final IPreferenceStore prop = Activator.getDefault().getPreferenceStore();
                    final String formatting = prop.getString(PreferenceConstants.FORMATTING);
                    if(Constants.EclipseDefault.equals(formatting)){
                        setting = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
                    }else if(Constants.Eclipse21.equals(formatting)){
                        setting = DefaultCodeFormatterConstants.getEclipse21Settings();
                    }else if(Constants.JavaConventions.equals(formatting)){
                        setting = DefaultCodeFormatterConstants.getJavaConventionsSettings();
                    }
                    final CodeFormatter formatter = ToolFactory.createCodeFormatter(setting,ToolFactory.M_FORMAT_EXISTING);
                    org.eclipse.text.edits.TextEdit edit = formatter.format(
                            CodeFormatter.K_UNKNOWN, cu.getSource(), 0,
                            cu.getSourceRange().getLength(), 0,
                            System.getProperty(Constants.SupportMapper_newLine));
                    if (edit != null) {
                        cu.applyTextEdit(edit, null);
                    }
                    if(javaClass==null){
                        cu.createPackageDeclaration(Constants.SupportMapper_defaultPackage, null);
                    }
                    cu.save(null, false);
                    return true;

        } catch (final JavaModelException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param rootPKG
     * @return 
     * @throws JavaModelException
     */
    private ICompilationUnit getCompilationUnitFromPKG(final String rootPKG) throws JavaModelException {
                IPackageFragment pack = getPack(rootPKG);
                if (pack == null){
                    IPackageFragmentRoot root = getRootPkg(rootPKG);
                    if (root==null) {
                        return null;
                    }
                    pack = root.createPackageFragment(Constants.SupportMapper_defaultPackage, false, null);
                }
                ICompilationUnit cu = pack.getCompilationUnit(Constants.SupportMapper_defaultFileName);
                if (!cu.exists()) {
                    cu = pack.createCompilationUnit(Constants.SupportMapper_defaultFileName, classdef, true,
                            null);
                }
                return cu;
    }
    /**
     * @param rootPKG
     * @return 
     * @throws JavaModelException
     */
    private IPackageFragment getPack(final String rootPKG) throws JavaModelException {
                IPackageFragmentRoot root = getRootPkg(rootPKG);
                if (root==null) {
                    return null;
                }
                IPackageFragment pack = null;
                for (final IJavaElement iJavaElement : root.getChildren()) {
                    if (iJavaElement.getPath().toString().replace(Constants.SupportMapper_slash, Constants.SupportMapper_dot)
                            .equals(rootPKG.replace(Constants.SupportMapper_slash,Constants.SupportMapper_dot)
                                    .replace(Constants.SupportMapper_Bkslash,Constants.SupportMapper_dot)+ Constants.SupportMapper_dot + Constants.SupportMapper_defaultPackage)) {
                        return pack = (IPackageFragment) iJavaElement;
                    }
                }
                return pack;
     }
    /**
     * @param rootPKG
     * @return 
     * @throws JavaModelException
     */
    private IPackageFragmentRoot getRootPkg(final String rootPKG) throws JavaModelException {
        final IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
        for (final IPackageFragmentRoot root : roots) {
            if (root.getRawClasspathEntry().getEntryKind() == IClasspathEntry.CPE_SOURCE
                    && (rootPKG ==null || root.getPath().toOSString().equals(rootPKG))) {
                return root;
            }
        }
        return null;
    }
    private void loadFunctionsFromCu(ICompilationUnit javaClass, String rootPKG, SimpleDataObjMapper sm) throws Throwable {
		if(javaClass==null){
			javaClass = getCompilationUnitFromPKG(rootPKG);
		}
		if(javaClass.getAllTypes().length==0){
			return;
		}
			for (IType type : javaClass.getAllTypes()) {
				if(type.getMethods().length==0){
					return;
				}
				for (IMethod method : type.getMethods()) {
					if(method.getNumberOfParameters()==1){
						String target = Signature.getReturnType(method.getSignature());
						target=Signature.getSignatureQualifier(target)+Constants.SupportMapper_dot+Signature.getSignatureSimpleName(target);
						String source = Signature.getParameterTypes(method.getSignature())[0];
						source=Signature.getSignatureQualifier(source)+Constants.SupportMapper_dot+Signature.getSignatureSimpleName(source);
						String fName = method.getElementName();
						sm.preLoadFunction(source, target, fName );
					}
				}
			}
	}
}
