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

    private static final char $ = "$".charAt(0);
    /**
     * Pom properties
     */
    private Map<String, String> properties = new HashMap<String, String>();
    /**
     * ArtiFact list
     */
    private HashMap<Entry<String, String>, Artifact> artiFactList = new HashMap<Entry<String, String>, Artifact>();

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
        public ArtifactEntry(String key, String value) {
            super(key, value);
        }

    }
    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#add(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public Artifact add(Artifact a) {
        completeArtifact(a);
        return fixArtifact(a);
    }

    /**
     * @param a
     * @return
     */
    private Artifact put(Artifact a) {
        return artiFactList.put(new ArtifactEntry(a.getGroup(), a.getArtifact()), a);
    }

    /**
     * @param a
     */
    private void completeArtifact(Artifact a) {
        if (a.getScope() == null) {
            a.setScope(Constants.COMPILE);
        }
        if (null == a.getVersion()) {
            String ver = properties.get(a.getArtifact() + "." + Constants.VERSION);
            if (null != ver) {
                a.setVersion(ver);
            }
        } else if (a.getVersion() != null && a.getVersion().startsWith("${")
                && properties.containsKey(a.getVersion().subSequence(2, a.getVersion().length() - 1))) {
            a.setVersion(properties.get(a.getVersion().subSequence(2, a.getVersion().length() - 1)));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#contains(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public boolean contains(Artifact a) {
        Entry<String, String> ga = getKey(a);
        boolean value = artiFactList.containsKey(ga);
        fixArtifact(a);
        return value;
    }

    /**
     * @param a
     * @param ga
     * @return
     */
    private Artifact fixArtifact(Artifact a) {
        Artifact artifact = a;
        Entry<String, String> ga = getKey(a);
        completeArtifact(a);
        if (artiFactList.containsKey(ga)) {
            artifact = artiFactList.get(ga);
            if ((Constants.COMPILE.equals(a.getScope()) || Constants.IMPORT.equals(a.getScope()))
                    && !Constants.COMPILE.equals(artifact.getScope())) {
                artifact.setScope(a.getScope());
            }
            if (artifact.getVersion() == null && a.getVersion() != null) {
                artifact.setVersion(a.getVersion());
            } else if (artifact.getVersion() != null && a.getVersion() != null
                    && artifact.getVersion().compareTo(a.getVersion()) < 0 && a.getVersion().charAt(0) != ArtifactsBlock.$)
                artifact.setVersion(a.getVersion());

        }
        return put(artifact);
    }

    /**
     * @param a
     * @return
     */
    private Entry<String, String> getKey(Artifact a) {
        Entry<String, String> ga = new ArtifactEntry(a.getGroup(), a.getArtifact());
        return ga;
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#get(java.lang.String, java.lang.String)
     */
    @Override
    public Artifact get(String g, String a) {
        return artiFactList.get(new ArtifactEntry(g, a));
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#get(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public Artifact get(Artifact a) {
        return artiFactList.get(new ArtifactEntry(a.getGroup(), a.getArtifact()));
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#values()
     */
    @Override
    public ArrayList<Artifact> values() {
        ArrayList<Artifact> result = new ArrayList<Artifact>(artiFactList.values());
        return result;
    }

    
    /*
     * (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#addProperties(java.util.Map)
     */
    @Override
    public void addProperties(Map<String, String> prop) {
        for (String key : prop.keySet()) {
            for (Artifact a : artiFactList.values()) {
                if (a.getVersion() != null && a.getVersion().startsWith("${")
                        && key.equals(a.getVersion().subSequence(2, a.getVersion().length() - 1))) {
                    a.setVersion(key);
                    add(a);
                }

            }
        }
        properties.putAll(prop);
    }

    @Override
    public boolean remove(Artifact a) {
        Entry<String, String> ga = getKey(a);
        Artifact artfact = artiFactList.remove(ga);
        return null!=artfact;
    }

    @Override
    public boolean removeAll(IArtifactsBlock a) {
        boolean ret = true;
        for (Artifact element : a.values()) {
            if(!remove(element)){
                ret= false;
            }
        }
        return ret;
    }

    @Override
    public void fixArtifacts(){
        for (Artifact it : artiFactList.values()) {
            completeArtifact(it);
        }
    }
}
