package org.smapgen.scl.repo.maven;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class Artifact {
private static final String FILE_SEPARATOR = System.getProperty("file.separator");
private String group;
private String artifact;
private String version;
private String scope;

/**
 * @return the scope
 */
protected String getScope() {
    return scope;
}
/**
 * @param scope the scope to set
 */
protected void setScope(String scope) {
    this.scope = scope;
}
/**
 * @return the group
 */
protected String getGroup() {
    return group;
}
/**
 * @param group the group to set
 */
protected void setGroup(String group) {
    this.group = group;
}
/**
 * @return the artifact
 */
protected String getArtifact() {
    return artifact;
}
/**
 * @param artifact the artifact to set
 */
protected void setArtifact(String artifact) {
    this.artifact = artifact;
}
/**
 * @return the version
 */
protected String getVersion() {
    return version;
}
/**
 * @param version the version to set
 */
protected void setVersion(String version) {
    this.version = version;
}

public String getPomName(){
    return getArtifact().concat("-").concat(getVersion()).concat(".pom");   
}

public String getRelativePath(){
    return  Artifact.FILE_SEPARATOR.concat(getGroup().replace(".", Artifact.FILE_SEPARATOR).concat(Artifact.FILE_SEPARATOR.concat(getArtifact())).concat(Artifact.FILE_SEPARATOR).concat(getVersion()!=null?getVersion():""));   
}
}
