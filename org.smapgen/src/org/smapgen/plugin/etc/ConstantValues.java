package org.smapgen.plugin.etc;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class ConstantValues extends NLS {
    public static String ClassMapper_elementPrefix;
    public static String ClassMapper_mapPrefix;
    public static String ClassMapper_newLine;
    private static final String BUNDLE_NAME = "org.smapgen.plugin.etc.constantValues"; //$NON-NLS-1$
    static {
        // initialize resource bundle
        NLS.initializeMessages(ConstantValues.BUNDLE_NAME, ConstantValues.class);
    }

    private ConstantValues() {}
}
