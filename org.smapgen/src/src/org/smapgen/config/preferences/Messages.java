package org.smapgen.config.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * Messages.
 */
public class Messages extends NLS {
    /**
     * String PreferencePage_0 .
     */
    public static String PreferencePage_0;
    /**
     * String PreferencePage_1 .
     */
    public static String PreferencePage_1;
    /**
     * String PreferencePage_2 .
     */
    public static String PreferencePage_2;
    /**
     * String PreferencePage_3 .
     */
    public static String PreferencePage_3;
    
    public static String PreferencePage_4;
    /**
     * String BUNDLE_NAME .
     */
    private static final String BUNDLE_NAME = "org.smapgen.config.preferences.messages";//$NON-NLS-1$
    /** $NON-NLS-1$. */
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * .
     * 
     * @return void
     */
    private Messages() {
    }
}
