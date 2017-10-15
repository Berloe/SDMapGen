package org.smapgen.scl.repo.maven;

import java.util.ArrayList;
import java.util.Map;

public interface IArtifactsBlock {

    /**
     * @param a
     * @return
     */
    Artifact add(Artifact a);

    /**
     * @param a
     * @return
     */
    boolean contains(Artifact a);

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
    Artifact get(Artifact a);

    /**
     * @return
     */
    ArrayList<Artifact> values();

    /**
     * @param prop
     */
    void addProperties(Map<String, String> prop);

}