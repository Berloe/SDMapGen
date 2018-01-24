package org.smapgen.scl.repo.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLStreamException;

import org.smapgen.scl.repo.IRepoProvider;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MvnRepoProvider implements IRepoProvider {
    private final IArtifactsBlock artifactBlock = new ArtifactsBlock();
    private final IArtifactsBlock artifactBlockBlackList = new ArtifactsBlock();
    private Path repo;
    private final ExecutorService threadPool = Executors.newWorkStealingPool();

    public MvnRepoProvider() {
        super();
    }

    /**
     * @param repo
     */
    public MvnRepoProvider(final Path repo) {
        super();
        this.repo = repo;
    }

    /**
     * @param artifact
     * @param file
     * @return
     */
    public File findPom(final Artifact artifact, final File file) {
        if (!file.exists()) {
            return null;
        }
        for (final File fileElement : file.listFiles()) {
            if (artifact.getVersion() != null) {
                if (fileElement.exists() && fileElement.getName().equals(artifact.getPomName())) {
                    return fileElement;
                }
            } else if (fileElement.isDirectory()) {
                return findPom(artifact, fileElement);
            } else if (fileElement.isFile() && fileElement.getName().startsWith(artifact.getArtifact()) && fileElement.getName().endsWith(".pom")) {
                return fileElement;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.IRepoProvider#getConfigFileName()
     */
    @Override
    public String getConfigFileName() {
        return "pom.xml";
    }

    /*
     * (non-Javadoc)
     * @see org.scl.repo.maven.IRepoProvider#getDependencies(java.io.File)
     */
    @Override
    public List<Artifact> getDependencies(final File conf) throws FileNotFoundException, XMLStreamException {
        return LoadPomDeps.loadDepsXmlFile(conf);
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.IRepoProvider#getDependenciesTree(java.io.File, java.nio.file.Path)
     */
    @Override
    public List<Artifact> getDependenciesTree(final File conf, final Path repo) throws FileNotFoundException, XMLStreamException {
        recursiveLoadDependencies(conf, repo);
        recursiveLoadExcludes(conf, repo);
        artifactBlock.removeAll(artifactBlockBlackList);
        threadPool.shutdown();
        while (!threadPool.isShutdown()) {
            try {
                threadPool.awaitTermination(1, TimeUnit.SECONDS);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        artifactBlock.fixArtifacts();
        return artifactBlock.values();
    }

    /*
     * (non-Javadoc)
     * @see org.scl.repo.maven.IRepoProvider#getResolveDependenciesURL(java.util.List, java.nio.file.Path)
     */
    @Override
    public Map<String, URI> getResolveDependenciesURL(final List<?> artifacts, final Path repo) throws FileNotFoundException, XMLStreamException {
        @SuppressWarnings("unchecked")
        final List<Artifact> artifactList = (List<Artifact>) artifacts;
        final Map<String, URI> uriList = new HashMap<>();
        for (final Artifact artifact : artifactList) {
            final File tmpFile = getDepPom(repo, artifact);
            if (tmpFile != null) {
                uriList.put(artifact.getArtifact(), tmpFile.getParentFile().toURI());
            }
        }
        return uriList;

    }

    /*
     * (non-Javadoc)
     * @see org.scl.repo.maven.IRepoProvider#getTransitiveDependencies(java.io.File)
     */
    @Override
    public List<Artifact> getTransitiveDependencies(final File conf) throws FileNotFoundException, XMLStreamException {
        final List<Artifact> deps2 = new ArrayList<>();
        final List<Artifact> deps = getDependencies(conf);
        if (deps.size() > 0) {
            for (final Artifact artifact : deps) {
                if (artifact.getArtifact() != null && artifact.getGroup() != null && artifact.getVersion() != null) {
                    final File file = getDepPom(repo, artifact);
                    if (null != file) {
                        final List<Artifact> auxDeps = getTransitiveDependencies(file);
                        for (final Artifact artifact2 : auxDeps) {
                            if (artifact2.getArtifact() != null && artifact2.getGroup() != null && artifact2.getVersion() != null && !deps.contains(artifact2) && !deps2.contains(artifact2)) {
                                deps2.add(artifact2);
                            }
                        }
                    }
                }
            }
        }
        deps2.addAll(deps);
        return deps2;
    }

    /**
     * @param conf
     * @param repo
     * @return
     * @throws FileNotFoundException
     */
    public void recursiveLoadDependencies(final File conf, final Path repo) throws FileNotFoundException {

        ArrayList<Artifact> deps = null;
        try {
            final Artifact parentArt = LoadPomDeps.loadParentXmlFile(conf);
            if (null != parentArt && !artifactBlock.contains(parentArt)) {
                addProperties(conf);
                final File parent = getDepPom(repo, parentArt);
                if (null != parent) {
                    recursiveLoadDependencies(parent, repo);
                }
            }
            deps = LoadPomDeps.loadDepsXmlFile(conf);

            for (final Artifact artifact : deps) {
                if (!artifactBlock.contains(artifact) && (null == artifact.getScope() || "compile".equals(artifact.getScope()) || "import".equals(artifact.getScope()))) {
                    addArtifact(artifact);
                    final File parent = getDepPom(repo, artifact);
                    if (null != parent) {
                        addProperties(parent);
                        final Runnable task = () -> {
                            try {
                                recursiveLoadDependencies(parent, repo);
                            } catch (final FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        };
                        threadPool.submit(task);
                    }
                }
            }
        } catch (final XMLStreamException e) {
            // Omit artifact
            deps = new ArrayList<>();
        }
    }

    /**
     * @param conf
     * @param repo
     * @return
     * @throws FileNotFoundException
     */
    public void recursiveLoadExcludes(final File conf, final Path repo) throws FileNotFoundException {

        ArrayList<Artifact> deps = null;
        try {
            final Artifact parentArt = LoadPomDeps.loadParentXmlFile(conf);
            if (null != parentArt && !artifactBlockBlackList.contains(parentArt)) {
                addPropertiesBlackList(conf);
                final File parent = getDepPom(repo, parentArt);
                if (null != parent) {
                    recursiveLoadDependencies(parent, repo);
                }
            }
            deps = LoadPomDeps.loadExcludedXmlFile(conf);
            for (final Artifact artifact : deps) {
                if (!artifactBlockBlackList.contains(artifact) && (null == artifact.getScope() || "compile".equals(artifact.getScope()) || "import".equals(artifact.getScope()))) {
                    addBlackListArtifact(artifact);
                    final File parent = getDepPom(repo, artifact);
                    if (null != parent) {
                        addPropertiesBlackList(parent);
                        final Runnable task = () -> {
                            try {
                                recursiveLoadDependencies(parent, repo);
                            } catch (final FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        };
                        threadPool.execute(task);
                    }
                }
            }
        } catch (final XMLStreamException e) {
            // Omit artifact
            deps = new ArrayList<>();
        }
    }

    /**
     * @param artifact
     */
    private synchronized void addArtifact(final Artifact artifact) {
        artifactBlock.add(artifact);
    }

    /**
     * @param artifact
     */
    private synchronized void addBlackListArtifact(final Artifact artifact) {
        artifactBlockBlackList.add(artifact);
    }

    /**
     * @param parent
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    private void addProperties(final File parent) throws FileNotFoundException, XMLStreamException {
        artifactBlock.addProperties(LoadPomDeps.loadVarsXmlFile(parent));
    }

    /**
     * @param conf
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    private synchronized void addPropertiesBlackList(final File conf) throws FileNotFoundException, XMLStreamException {
        artifactBlockBlackList.addProperties(LoadPomDeps.loadVarsXmlFile(conf));
    }

    /**
     * @param repo
     * @param artifact
     * @return
     */
    private File getDepPom(final Path repo, final Artifact artifact) {
        final File file = new File(repo.toString().concat(artifact.getRelativePath()));
        return findPom(artifact, file);
    }

}
