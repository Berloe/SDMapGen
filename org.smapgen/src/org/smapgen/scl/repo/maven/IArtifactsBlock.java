package org.smapgen.scl.repo.maven;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public interface IArtifactsBlock {

    /**
     * @param a
     * @return
     */
    Artifact add(Artifact a);

    /**
     * @param prop
     */
    void addProperties(Map<String, String> prop);

    /**
     * @param a
     * @return
     */
    boolean contains(Artifact a);

    void fixArtifacts();

    /**
     * @param a
     * @return
     */
    Artifact get(Artifact a);

    /**
     * @param g
     * @param a
     * @return
     */
    Artifact get(String g, String a);

    /**
     * @param a
     * @return
     */
    boolean remove(Artifact a);

    /**
     * @param a
     * @return
     */
    boolean removeAll(IArtifactsBlock a);

    /**
     * @return
     */
    ArrayList<Artifact> values();

}