package org.smapgen.scl.repo.maven;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class Artifact {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private String artifact;
    private String group;
    private String scope;
    private String version;

    public String getPomName() {
        return getArtifact().concat("-").concat(getVersion()).concat(".pom");
    }

    public String getRelativePath() {
        return Artifact.FILE_SEPARATOR.concat(getGroup().replace(".", Artifact.FILE_SEPARATOR).concat(Artifact.FILE_SEPARATOR.concat(getArtifact())).concat(Artifact.FILE_SEPARATOR).concat(getVersion() != null ? getVersion() : ""));
    }

    /**
     * @return the artifact
     */
    protected String getArtifact() {
        return artifact;
    }

    /**
     * @return the group
     */
    protected String getGroup() {
        return group;
    }

    /**
     * @return the scope
     */
    protected String getScope() {
        return scope;
    }

    /**
     * @return the version
     */
    protected String getVersion() {
        return version;
    }

    /**
     * @param artifact
     *            the artifact to set
     */
    protected void setArtifact(final String artifact) {
        this.artifact = artifact;
    }

    /**
     * @param group
     *            the group to set
     */
    protected void setGroup(final String group) {
        this.group = group;
    }

    /**
     * @param scope
     *            the scope to set
     */
    protected void setScope(final String scope) {
        this.scope = scope;
    }

    /**
     * @param version
     *            the version to set
     */
    protected void setVersion(final String version) {
        this.version = version;
    }
}
