package org.smapgen.scl.exception;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ClassLoaderException extends Throwable {

    private static final long serialVersionUID = 1L;

    private String className;
    private String classCanonicalName;

    public ClassLoaderException() {}

    public ClassLoaderException(final String classCanonicalName) {
        if (classCanonicalName == null || classCanonicalName.trim().length() <= 0) {
            return;
        }
        classCanonicalName.replace('/', '.');
        setClassCanonicalName(classCanonicalName);
        setClassName(classCanonicalName.substring(classCanonicalName.lastIndexOf('.'), classCanonicalName.length()));
    }

    public ClassLoaderException(final String classCanonicalName, final Throwable cause) {
        super(cause.getMessage(), cause);
        if (classCanonicalName == null || classCanonicalName.trim().length() <= 0) {
            return;
        }
        setClassCanonicalName(classCanonicalName.replace('/', '.'));
        setClassName(classCanonicalName.substring(classCanonicalName.lastIndexOf('.'), classCanonicalName.length()));
    }

    public ClassLoaderException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ClassLoaderException(final Throwable cause) {
        super(cause);
    }

    /**
     * @return the classCanonicalName
     */
    public String getClassCanonicalName() {
        return classCanonicalName;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param classCanonicalName
     *            the classCanonicalName to set
     */
    public void setClassCanonicalName(final String classCanonicalName) {
        this.classCanonicalName = classCanonicalName;
    }

    /**
     * @param className
     *            the className to set
     */
    public void setClassName(final String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        final String s = "Class load error";
        final String message = classCanonicalName;
        return message != null ? s + ": " + message + " or some dependency could not be found" : s;
    }
}
