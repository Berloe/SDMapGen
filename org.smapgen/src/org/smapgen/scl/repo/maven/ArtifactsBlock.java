package org.smapgen.scl.repo.maven;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ArtifactsBlock implements IArtifactsBlock {

    /**
     * @author Alberto Fuentes Gómez
     *
     */
    @SuppressWarnings("serial")
    private class ArtifactEntry extends SimpleEntry<String, String> {

        /**
         * @param key
         * @param value
         */
        public ArtifactEntry(final String key, final String value) {
            super(key, value);
        }

    }

    private static final char $ = "$".charAt(0);
    /**
     * Pom properties
     */
    private final Map<String, String> properties = new HashMap<>();

    /**
     * ArtiFact list
     */
    private final HashMap<Entry<String, String>, Artifact> artiFactList = new HashMap<>();

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#add(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public Artifact add(final Artifact a) {
        completeArtifact(a);
        return fixArtifact(a);
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#addProperties(java.util.Map)
     */
    @Override
    public void addProperties(final Map<String, String> prop) {
        properties.putAll(prop);
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#contains(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public boolean contains(final Artifact a) {
        final Entry<String, String> ga = getKey(a);
        final boolean value = artiFactList.containsKey(ga);
        fixArtifact(a);
        return value;
    }

    @Override
    public void fixArtifacts() {
        final Artifact[] artifacts = artiFactList.values().toArray(new Artifact[0]);
        for (final Artifact it : artifacts) {
            completeArtifact(it);
            artiFactList.put(getKey(it), it);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#get(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public Artifact get(final Artifact a) {
        return artiFactList.get(new ArtifactEntry(a.getGroup(), a.getArtifact()));
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#get(java.lang.String, java.lang.String)
     */
    @Override
    public Artifact get(final String g, final String a) {
        return artiFactList.get(new ArtifactEntry(g, a));
    }

    @Override
    public boolean remove(final Artifact a) {
        final Entry<String, String> ga = getKey(a);
        final Artifact artfact = artiFactList.remove(ga);
        return null != artfact;
    }

    @Override
    public boolean removeAll(final IArtifactsBlock a) {
        boolean ret = true;
        for (final Artifact element : a.values()) {
            if (!remove(element)) {
                ret = false;
            }
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#values()
     */
    @Override
    public ArrayList<Artifact> values() {
        final ArrayList<Artifact> result = new ArrayList<>(artiFactList.values());
        return result;
    }

    /**
     * @param a
     */
    private void completeArtifact(final Artifact a) {
        if (a.getScope() == null) {
            a.setScope(Constants.COMPILE);
        }
        if (null == a.getVersion()) {
            final String ver = properties.get(a.getArtifact() + "." + Constants.VERSION);
            if (null != ver) {
                a.setVersion(ver);
            }
        } else if (a.getVersion() != null && a.getVersion().startsWith("${") && properties.containsKey(a.getVersion().subSequence(2, a.getVersion().length() - 1))) {
            a.setVersion(properties.get(a.getVersion().subSequence(2, a.getVersion().length() - 1)));
        }
    }

    /**
     * @param a
     * @param ga
     * @return
     */
    private Artifact fixArtifact(final Artifact a) {
        Artifact artifact = a;
        final Entry<String, String> ga = getKey(a);
        completeArtifact(a);
        if (artiFactList.containsKey(ga)) {
            artifact = artiFactList.get(ga);
            if ((Constants.COMPILE.equals(a.getScope()) || Constants.IMPORT.equals(a.getScope())) && !Constants.COMPILE.equals(artifact.getScope())) {
                artifact.setScope(a.getScope());
            }
            if (artifact.getVersion() == null && a.getVersion() != null) {
                artifact.setVersion(a.getVersion());
            } else if (artifact.getVersion() != null && a.getVersion() != null && artifact.getVersion().compareTo(a.getVersion()) < 0 && a.getVersion().charAt(0) != ArtifactsBlock.$) {
                artifact.setVersion(a.getVersion());
            }

        }
        return put(artifact);
    }

    /**
     * @param a
     * @return
     */
    private Entry<String, String> getKey(final Artifact a) {
        final Entry<String, String> ga = new ArtifactEntry(a.getGroup(), a.getArtifact());
        return ga;
    }

    /**
     * @param a
     * @return
     */
    private Artifact put(final Artifact a) {
        return artiFactList.put(new ArtifactEntry(a.getGroup(), a.getArtifact()), a);
    }
}
