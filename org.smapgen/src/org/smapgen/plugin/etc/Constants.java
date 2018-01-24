package org.smapgen.plugin.etc;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class Constants {
    public static String Eclipse21 = "Eclipse21";
    public static String EclipseDefault = "EclipseDefault";
    public static String JavaConventions = "JavaConventions";
    public static String SupportMapper_Bkslash = "\\";
    public static String SupportMapper_cacheFile = "/ClassMap.ser";
    public static String SupportMapper_defaultFile = "PlainMapper";
    public static String SupportMapper_startTag = "{";
    public static String SupportMapper_classHeader = "public class " + Constants.SupportMapper_defaultFile + Constants.SupportMapper_startTag;
    public static String SupportMapper_defaultFileName = Constants.SupportMapper_defaultFile + ".java";
    public static String SupportMapper_defaultPackage = "mapping";
    public static String SupportMapper_dot = ".";
    public static String SupportMapper_endTag = "}";
    public static String SupportMapper_newLine = "line.separator";
    public static String SupportMapper_slash = "/";

    /**
     * constructor
     */
    private Constants() {
        super();
    }
}
