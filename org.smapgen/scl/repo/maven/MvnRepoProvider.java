package org.smapgen.scl.repo.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.smapgen.scl.repo.IRepoProvider;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MvnRepoProvider implements IRepoProvider {
    /**
     * @param repo
     */
    public MvnRepoProvider(Path repo) {
        super();
        this.repo = repo;
    }
    public MvnRepoProvider() {
        super();
    }
    private Path repo;

    private IArtifactsBlock artifactBlock = null;
    /* (non-Javadoc)
     * @see org.scl.repo.maven.IRepoProvider#getDependencies(java.io.File)
     */
    @Override
    public List<Artifact> getDependencies(File conf) throws FileNotFoundException, XMLStreamException{
        return LoadPomDeps.loadDepsXmlFile(conf);
    }
    /* (non-Javadoc)
     * @see org.smapgen.scl.repo.IRepoProvider#getDependenciesTree(java.io.File, java.nio.file.Path)
     */
    @Override
    public List<Artifact> getDependenciesTree(File conf,Path repo) throws FileNotFoundException, XMLStreamException{
        artifactBlock = new ArtifactsBlock();
        recursiveLoadDependencies(conf, repo);
 
        return artifactBlock.values();
    }
    /**
     * @param conf
     * @param repo
     * @return
     * @throws FileNotFoundException
     */
    public void recursiveLoadDependencies(File conf, Path repo) throws FileNotFoundException {

        ArrayList<Artifact> deps = null;
        try{
            Artifact parentArt = LoadPomDeps.loadParentXmlFile(conf);
            if(null != parentArt && !artifactBlock.contains(parentArt)){
                artifactBlock.addProperties(LoadPomDeps.loadVarsXmlFile(conf));
                File parent = getDepPom(repo, parentArt);
                if(null != parent){
                    recursiveLoadDependencies(parent,repo);
                }
            }
            deps = LoadPomDeps.loadDepsXmlFile(conf);
            for (Artifact artifact : deps) {
                if(artifact.getArtifact().equals("jaxb2-basics-runtime")){
                    artifact.getArtifact();
                }
                if(!artifactBlock.contains(artifact) && (null ==artifact.getScope()||"compile".equals(artifact.getScope())|| "import".equals(artifact.getScope()))){
                    artifactBlock.add(artifact);
                    File parent = getDepPom(repo, artifact);
                    if(null != parent){
                        artifactBlock.addProperties(LoadPomDeps.loadVarsXmlFile(parent));
                        recursiveLoadDependencies(parent,repo);
                    }
                }
            }
        }catch(XMLStreamException e){
            // Omit artifact
            deps= new ArrayList<Artifact>();
        }
    }
    
    
    /* (non-Javadoc)
     * @see org.scl.repo.maven.IRepoProvider#getTransitiveDependencies(java.io.File)
     */
    @Override
    public List<Artifact> getTransitiveDependencies(File conf) throws FileNotFoundException, XMLStreamException{
            List<Artifact> deps2 = new ArrayList<Artifact>();
            List<Artifact> deps = getDependencies(conf);
            if (deps.size()>0){
                for (Artifact artifact : deps) {
                    if(artifact.getArtifact()!=null && artifact.getGroup()!=null && artifact.getVersion()!=null){
                        File file = getDepPom(repo, artifact);
                        List<Artifact> auxDeps = getTransitiveDependencies(file);
                        for (Artifact artifact2 : auxDeps) {
                            if(artifact2.getArtifact()!=null && artifact2.getGroup()!=null && artifact2.getVersion()!=null && !deps.contains(artifact2)&&!deps2.contains(artifact2)) {
                                deps2.add(artifact2);
                            }
                        }
                    }
                }
            }
            deps2.addAll(deps);
            return deps2;
    }
    /* (non-Javadoc)
     * @see org.scl.repo.maven.IRepoProvider#getResolveDependenciesURL(java.util.List, java.nio.file.Path)
     */
    @Override
    public Map<String,URI> getResolveDependenciesURL(List<?> artifacts,Path repo) throws FileNotFoundException, XMLStreamException{
        @SuppressWarnings("unchecked")
        List<Artifact> artifactList = (List<Artifact>) artifacts;
        Map<String,URI> uriList = new HashMap<String, URI>();
        for (Artifact artifact : artifactList) {
            File tmpFile = getDepPom(repo, artifact);
            if(tmpFile!=null){
                uriList.put(artifact.getArtifact(),tmpFile.getParentFile().toURI());
            }
        }
        return uriList;

   }
    /**
     * @param repo
     * @param artifact
     * @return
     */
    private File getDepPom(Path repo, Artifact artifact) {
        File file = new File(repo.toString().concat(artifact.getRelativePath()));
        return findPom(artifact, file);
    }
    /**
     * @param artifact
     * @param file
     * @return
     */
    public File findPom(Artifact artifact, File file) {
        if(!file.exists()){
            return null;
        }
        for (final File fileElement : file.listFiles()){
            if(artifact.getVersion()!=null){
                if (fileElement.exists() && fileElement.getName().equals(artifact.getPomName())) {
                    return fileElement;
                }
            } else if(fileElement.isDirectory()) {
                return findPom(artifact, fileElement);
            } else if(fileElement.isFile() && fileElement.getName().startsWith(artifact.getArtifact()) && fileElement.getName().endsWith(".pom")) {
                return fileElement;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.smapgen.scl.repo.IRepoProvider#getConfigFileName()
     */
    @Override
    public String getConfigFileName() {
        return "pom.xml";
    }
    
}
