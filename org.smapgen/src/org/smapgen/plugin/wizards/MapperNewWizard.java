package org.smapgen.plugin.wizards;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.smapgen.config.preferences.PreferenceConstants;
import org.smapgen.plugin.Activator;
import org.smapgen.plugin.etc.MapperWizardHelper;
import org.smapgen.scl.SimpleClassLoader;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MapperNewWizard extends Wizard implements INewWizard {
    private ICompilationUnit javaclass;
    /**
     * String[] selectionData .
     */
    private String[] selectionData;
    /**
     * SupportMapper suport .
     */
    private final MapperWizardHelper suport;
    /**
     * 
     * @param IJavaProject
     *            project.
     * @return Set IJavaProject
     */
    public static Set<IJavaProject> getAllRequiredProjects(final IJavaProject p) {
        final Set<IJavaProject> $ = new HashSet<IJavaProject>();
        try {
            for (final String reqName : p.getRequiredProjectNames()) {
                final IJavaProject reqProject = getJavaProjectByName(reqName);
                // also add recursive requirements
                if (reqProject != null) {
                    $.addAll(getAllRequiredProjects(reqProject));
                    $.add(reqProject);
                }
            }
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }

        return $;
    }

    /**
     * 
     * @param String
     *            name.
     * @return IJavaProject
     */
    public static IJavaProject getJavaProjectByName(final String name) {
        final IWorkspaceRoot wr = ResourcesPlugin.getWorkspace().getRoot();
        final IProject project = wr.getProject(name);
        if (project == null)
            throw new IllegalArgumentException("Project " + name + " not found");
        if (!project.isOpen())
            try {
                project.open(null);
            } catch (final CoreException e) {
                // Si no puede abrirlo no hace nada y retorna null
                return null;
            }
        IJavaProject $ = null;
        try {
            $ = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
        } catch (final CoreException e) {
            e.printStackTrace();
        }
        if ($ == null)
            throw new IllegalArgumentException("Project " + name + " not a java project");

        return $;
    }

    /**
     * Constructor for MapperNewWizard.
     */
    public MapperNewWizard() {
        setNeedsProgressMonitor(true);
        suport = new MapperWizardHelper();
    }

    /**
     * Adding the page to the wizard.
     */

    @Override
    public void addPages() {
        try {
            MapperNewWizardPage page = new MapperNewWizardPage(selectionData, suport,javaclass);
            addPage(page);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    @Override
    public void init(final IWorkbench w, final IStructuredSelection s) {
        if (s instanceof IStructuredSelection)
            for (final Iterator<?> it = s.iterator(); it.hasNext();) {
                final Object element = it.next();
                if (element instanceof IProject)
                    suport.setProject(JavaCore.create((IProject) element));
                else if (element instanceof IJavaProject)
                    suport.setProject((IJavaProject) element);
                else if (element instanceof IPackageFragmentRoot)
                    suport.setProject(((IPackageFragmentRoot) element).getJavaProject());
                else if (element instanceof IPackageFragment)
                    suport.setProject(((IPackageFragment) element).getJavaProject());
                else if (element instanceof IJavaProject)
                    suport.setProject((IJavaProject) element);
                else if (element instanceof IAdaptable)
                    if((IJavaProject) ((IAdaptable) element).getAdapter(IProject.class)!=null){
                        suport.setProject((IJavaProject) ((IAdaptable) element).getAdapter(IProject.class));
                    }else if((IFile) ((IAdaptable) element).getAdapter(IFile.class)!=null){
                        javaclass=JavaCore.createCompilationUnitFrom((IFile) ((IAdaptable) element).getAdapter(IFile.class));
                        suport.setProject(javaclass.getJavaProject());
                    }
                
                if (suport.getProject() != null) {
                    final Set<IJavaProject> projectTree = getAllRequiredProjects(suport.getProject());
                    projectTree.add(suport.getProject());
                    for (final IJavaProject iJavaProject : projectTree)
                        try {
                            initClassPaths(iJavaProject);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                }
            }
        try {
            selectionData = suport.getSimpleCl().getLoadedClassNames();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for (int i = 0; i < selectionData.length; ++i) {
            final String string = selectionData[i].replace('/', '.');
            selectionData[i] = string;
        }

    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using
     * wizard as execution context.
     */
    @Override
    public boolean performFinish() {
        return true;
    }

    /**
     * 
     * @param IJavaProject
     *            project.
     * @return void
     * @throws Throwable 
     */
    private void initClassPaths(final IJavaProject p) throws Throwable {
        final IClasspathEntry[] resolvedClass = p.getResolvedClasspath(true);
        final String[] importer = p.getRequiredProjectNames();
        for (final IClasspathEntry iClasspathEntry : resolvedClass)
            if (iClasspathEntry.getOutputLocation() != null){
                suport.getSimpleCl()
                        .loadlib(p.getProject().getLocation().toOSString()
                                + iClasspathEntry.getOutputLocation().toOSString()
                                        .substring(iClasspathEntry.getOutputLocation().toOSString().indexOf('\\', 1)));
            }
            else{
                for (final String importPrj : importer)
                    if (iClasspathEntry.getPath().toOSString().contains(importPrj))
                        suport.getSimpleCl().loadlib(iClasspathEntry.getPath().toOSString());
            }
        suport.getSimpleCl().loadlib(p.getProject().getLocation().toOSString()
                + p.getOutputLocation().toOSString().substring(p.getOutputLocation().toOSString().indexOf('\\', 1)));
        loadDependencies();
    }

    /**
     * 
     */
    private void loadDependencies() {
        final IPreferenceStore prop = Activator.getDefault().getPreferenceStore();
        final String value = prop.getString(PreferenceConstants.P_PATHCLASSREPO);
        final SimpleClassLoader reposcl = new SimpleClassLoader(value);
        // Init classes into classloader
        try {
            Map<String, File> dependencies = suport.getResolvedDependendencyTree(suport.getRepoConfig(suport.getProject()), value);
            for (File f : dependencies.values()) {
                reposcl.loadlib(f.getPath());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        suport.getSimpleCl().setRepoClassLoader(reposcl);
    }
    
}