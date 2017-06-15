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
    private Map<String,String> properties = new HashMap<String, String>();
    HashMap<Entry<String, String>,Artifact> artiFactList = new HashMap<Entry<String, String>, Artifact>();
    /* (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#add(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public Artifact add(Artifact a){
       completeArtifact(a);
       return artiFactList.put(new ArtifactEntry(a.getGroup(), a.getArtifact()),a);
    }
    
    /**
     * @param a
     */
    private void completeArtifact(Artifact a) {
        if (a.getScope()== null){
            a.setScope("compile");
        }
        if(null == a.getVersion()){
            String ver = properties.get(a.getArtifact()+".version");
            if(null != ver){
                a.setVersion(ver);
            }
        }else if(a.getVersion()!=null && a.getVersion().startsWith("${")){
            if(properties.containsKey(a.getVersion().subSequence(2, a.getVersion().length()-1))){
                a.setVersion(properties.get(a.getVersion().subSequence(2, a.getVersion().length()-1)));
            }
        }
    }
    /* (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#contains(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public boolean contains(Artifact a){
        Entry<String, String> ga = new ArtifactEntry(a.getGroup(),a.getArtifact());
        boolean value = artiFactList.containsKey(ga);
        fixArtifact(a);
        return value;
    }

    /**
     * @param a
     * @param ga
     */
    private void fixArtifact(Artifact a) {
        Artifact artifact = a;
        Entry<String, String> ga = new ArtifactEntry(a.getGroup(),a.getArtifact());
        completeArtifact(a);
        if(artiFactList.containsKey(ga)){
            artifact = artiFactList.get(ga);
            if ((a.getScope().equals("compile")||a.getScope().equals("import")) && !artifact.getScope().equals("compile")){
                artifact.setScope(a.getScope());
            }
            if(artifact.getVersion()==null && a.getVersion()!=null){
                artifact.setVersion(a.getVersion());
            }else if(artifact.getVersion()!=null && a.getVersion()!=null && artifact.getVersion().compareTo(a.getVersion())<0){
                if(!a.getVersion().startsWith("$"))
                    artifact.setVersion(a.getVersion());
            }
        }
        add(artifact);
    }
    /* (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#get(java.lang.String, java.lang.String)
     */
    @Override
    public Artifact get(String g, String a){
        return artiFactList.get(new ArtifactEntry(g, a));
    }
    /* (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#get(org.smapgen.scl.repo.maven.Artifact)
     */
    @Override
    public Artifact get(Artifact a){
        return artiFactList.get(new ArtifactEntry(a.getGroup(), a.getArtifact()));
    }
    /* (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#values()
     */
    @Override
    public ArrayList<Artifact> values(){
        ArrayList<Artifact> result = new ArrayList<Artifact>(artiFactList.values());
        return result;
    }
    /* (non-Javadoc)
     * @see org.smapgen.scl.repo.maven.IArtifactsBlock#addProperties(java.util.Map)
     */
    @Override
    public void addProperties(Map<String,String> prop){
        for (String key : prop.keySet()) {
            for (Artifact a : artiFactList.values()) {
                if(a.getVersion()!=null && a.getVersion().startsWith("${")){
                    if(key.equals(a.getVersion().subSequence(2, a.getVersion().length()-1))){
                        a.setVersion(key);
                        add(a);
                    }
                }
            }
        }
        properties.putAll(prop);
    }
}
