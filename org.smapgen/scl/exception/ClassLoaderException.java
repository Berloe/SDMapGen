package org.smapgen.scl.exception;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ClassLoaderException extends Throwable {

    private static final long serialVersionUID = 1L;

    private String className;
    private String classCanonicalName;

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className
     *            the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the classCanonicalName
     */
    public String getClassCanonicalName() {
        return classCanonicalName;
    }

    /**
     * @param classCanonicalName
     *            the classCanonicalName to set
     */
    public void setClassCanonicalName(String classCanonicalName) {
        this.classCanonicalName = classCanonicalName;
    }

    public ClassLoaderException() {
    }

    public ClassLoaderException(String classCanonicalName) {
        if (classCanonicalName == null || classCanonicalName.trim().length() <= 0)
            return;
        classCanonicalName.replace('/', '.');
        setClassCanonicalName(classCanonicalName);
        setClassName(classCanonicalName.substring(classCanonicalName.lastIndexOf('.'), classCanonicalName.length()));
    }

    public ClassLoaderException(String classCanonicalName, Throwable cause) {
        super(cause.getMessage(), cause);
        if (classCanonicalName == null || classCanonicalName.trim().length() <= 0)
            return;
        classCanonicalName=classCanonicalName.replace('/', '.');
        setClassCanonicalName(classCanonicalName);
        setClassName(classCanonicalName.substring(classCanonicalName.lastIndexOf('.'), classCanonicalName.length()));
    }

    public ClassLoaderException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ClassLoaderException(Throwable cause) {
        super(cause);
    }

}
