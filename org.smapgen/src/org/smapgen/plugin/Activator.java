package org.smapgen.plugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

    /**
     * String PLUGIN_ID .
     */
    // The plug-in ID
    public static final String PLUGIN_ID = "javaMappingGenerator"; //$NON-NLS-1$
    /**
     * Activator plugin .
     */
    // The shared instance
    private static Activator plugin;

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     *
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(final String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * 
     * @param BundleContext
     *            context.
     * @return void
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext c) throws Exception {
        super.start(c);
        plugin = this;
    }

    /**
     * 
     * @param BundleContext
     *            context.
     * @return void
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext c) throws Exception {
        plugin = null;
        super.stop(c);
    }
}
