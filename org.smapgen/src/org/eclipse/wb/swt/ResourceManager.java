/*******************************************************************************
 * Copyright (c) 2011 Google, Inc. All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.wb.swt;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.osgi.framework.Bundle;

/**
 * Utility class for managing OS resources associated with SWT/JFace controls such as colors, fonts, images, etc.
 * 
 * !!! IMPORTANT !!! Application code must explicitly invoke the <code>dispose()</code> method to release the operating system resources managed by cached objects when those objects and OS resources
 * are no longer needed (e.g. on application shutdown)
 * 
 * This class may be freely distributed as part of any application or plugin.
 * <p>
 * 
 * @author scheglov_ke
 * @author Dan Rubel
 */
public class ResourceManager extends SWTResourceManager {
    /**
     * Provider for plugin resources, used by WindowBuilder at design time.
     */
    public interface PluginResourceProvider {
        URL getEntry(String symbolicName, String path);
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Image
    //
    ////////////////////////////////////////////////////////////////////////////
    private static Map<ImageDescriptor, Image> m_descriptorImageMap = new HashMap<>();
    /**
     * Maps images to decorated images.
     */
    @SuppressWarnings("unchecked")
    private static Map<Image, Map<Image, Image>>[] m_decoratedImageMap = new Map[SWTResourceManager.LAST_CORNER_KEY];
    ////////////////////////////////////////////////////////////////////////////
    //
    // Plugin images support
    //
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Maps URL to images.
     */
    private static Map<String, Image> m_URLImageMap = new HashMap<>();
    /**
     * Instance of {@link PluginResourceProvider}, used by WindowBuilder at design time.
     */
    private static PluginResourceProvider m_designTimePluginResourceProvider = null;

    /**
     * Returns an {@link Image} composed of a base image decorated by another image.
     * 
     * @param baseImage
     *            the base {@link Image} that should be decorated.
     * @param decorator
     *            the {@link Image} to decorate the base image.
     * @return {@link Image} The resulting decorated image.
     */
    public static Image decorateImage(final Image baseImage, final Image decorator) {
        return ResourceManager.decorateImage(baseImage, decorator, SWTResourceManager.BOTTOM_RIGHT);
    }

    /**
     * Returns an {@link Image} composed of a base image decorated by another image.
     * 
     * @param baseImage
     *            the base {@link Image} that should be decorated.
     * @param decorator
     *            the {@link Image} to decorate the base image.
     * @param corner
     *            the corner to place decorator image.
     * @return the resulting decorated {@link Image}.
     */
    public static Image decorateImage(final Image baseImage, final Image decorator, final int corner) {
        if (corner <= 0 || corner >= SWTResourceManager.LAST_CORNER_KEY) {
            throw new IllegalArgumentException("Wrong decorate corner");
        }
        Map<Image, Map<Image, Image>> cornerDecoratedImageMap = ResourceManager.m_decoratedImageMap[corner];
        if (cornerDecoratedImageMap == null) {
            cornerDecoratedImageMap = new HashMap<>();
            ResourceManager.m_decoratedImageMap[corner] = cornerDecoratedImageMap;
        }
        Map<Image, Image> decoratedMap = cornerDecoratedImageMap.get(baseImage);
        if (decoratedMap == null) {
            decoratedMap = new HashMap<>();
            cornerDecoratedImageMap.put(baseImage, decoratedMap);
        }
        //
        Image result = decoratedMap.get(decorator);
        if (result == null) {
            final Rectangle bib = baseImage.getBounds();
            final Rectangle dib = decorator.getBounds();
            final Point baseImageSize = new Point(bib.width, bib.height);
            final CompositeImageDescriptor compositImageDesc = new CompositeImageDescriptor() {
                @SuppressWarnings("deprecation")
                @Override
                protected void drawCompositeImage(final int width, final int height) {
                    drawImage(baseImage.getImageData(), 0, 0);
                    if (corner == SWTResourceManager.TOP_LEFT) {
                        drawImage(decorator.getImageData(), 0, 0);
                    } else if (corner == SWTResourceManager.TOP_RIGHT) {
                        drawImage(decorator.getImageData(), bib.width - dib.width, 0);
                    } else if (corner == SWTResourceManager.BOTTOM_LEFT) {
                        drawImage(decorator.getImageData(), 0, bib.height - dib.height);
                    } else if (corner == SWTResourceManager.BOTTOM_RIGHT) {
                        drawImage(decorator.getImageData(), bib.width - dib.width, bib.height - dib.height);
                    }
                }

                @Override
                protected Point getSize() {
                    return baseImageSize;
                }
            };
            //
            result = compositImageDesc.createImage();
            decoratedMap.put(decorator, result);
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // General
    //
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Dispose of cached objects and their underlying OS resources. This should only be called when the cached objects are no longer needed (e.g. on application shutdown).
     */
    public static void dispose() {
        SWTResourceManager.disposeColors();
        SWTResourceManager.disposeFonts();
        ResourceManager.disposeImages();
    }

    /**
     * Dispose all of the cached images.
     */
    public static void disposeImages() {
        SWTResourceManager.disposeImages();
        // dispose ImageDescriptor images
        {
            for (final Image image : ResourceManager.m_descriptorImageMap.values()) {
                image.dispose();
            }
            ResourceManager.m_descriptorImageMap.clear();
        }
        // dispose decorated images
        for (final Map<Image, Map<Image, Image>> cornerDecoratedImageMap : ResourceManager.m_decoratedImageMap) {
            if (cornerDecoratedImageMap != null) {
                for (final Map<Image, Image> decoratedMap : cornerDecoratedImageMap.values()) {
                    for (final Image image : decoratedMap.values()) {
                        image.dispose();
                    }
                    decoratedMap.clear();
                }
                cornerDecoratedImageMap.clear();
            }
        }
        // dispose plugin images
        {
            for (final Image image : ResourceManager.m_URLImageMap.values()) {
                image.dispose();
            }
            ResourceManager.m_URLImageMap.clear();
        }
    }

    /**
     * Returns an {@link Image} based on the specified {@link ImageDescriptor}.
     * 
     * @param descriptor
     *            the {@link ImageDescriptor} for the {@link Image}.
     * @return the {@link Image} based on the specified {@link ImageDescriptor}.
     */
    public static Image getImage(final ImageDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        Image image = ResourceManager.m_descriptorImageMap.get(descriptor);
        if (image == null) {
            image = descriptor.createImage();
            ResourceManager.m_descriptorImageMap.put(descriptor, image);
        }
        return image;
    }

    /**
     * Returns an {@link ImageDescriptor} stored in the file at the specified path relative to the specified class.
     * 
     * @param clazz
     *            the {@link Class} relative to which to find the image descriptor.
     * @param path
     *            the path to the image file.
     * @return the {@link ImageDescriptor} stored in the file at the specified path.
     */
    public static ImageDescriptor getImageDescriptor(final Class<?> clazz, final String path) {
        return ImageDescriptor.createFromFile(clazz, path);
    }

    /**
     * Returns an {@link ImageDescriptor} stored in the file at the specified path.
     * 
     * @param path
     *            the path to the image file.
     * @return the {@link ImageDescriptor} stored in the file at the specified path.
     */
    public static ImageDescriptor getImageDescriptor(final String path) {
        try {
            return ImageDescriptor.createFromURL(new File(path).toURI().toURL());
        } catch (final MalformedURLException e) {
            return null;
        }
    }

    /**
     * Returns an {@link Image} based on a plugin and file path.
     * 
     * @param plugin
     *            the plugin {@link Object} containing the image
     * @param name
     *            the path to the image within the plugin
     * @return the {@link Image} stored in the file at the specified path
     * 
     * @deprecated Use {@link #getPluginImage(String, String)} instead.
     */
    @Deprecated
    public static Image getPluginImage(final Object plugin, final String name) {
        try {
            final URL url = ResourceManager.getPluginImageURL(plugin, name);
            if (url != null) {
                return ResourceManager.getPluginImageFromUrl(url);
            }
        } catch (final Throwable e) {
            // Ignore any exceptions
        }
        return null;
    }

    /**
     * Returns an {@link Image} based on a {@link Bundle} and resource entry path.
     * 
     * @param symbolicName
     *            the symbolic name of the {@link Bundle}.
     * @param path
     *            the path of the resource entry.
     * @return the {@link Image} stored in the file at the specified path.
     */
    public static Image getPluginImage(final String symbolicName, final String path) {
        try {
            final URL url = ResourceManager.getPluginImageURL(symbolicName, path);
            if (url != null) {
                return ResourceManager.getPluginImageFromUrl(url);
            }
        } catch (final Throwable e) {
            // Ignore any exceptions
        }
        return null;
    }

    /**
     * Returns an {@link ImageDescriptor} based on a plugin and file path.
     * 
     * @param plugin
     *            the plugin {@link Object} containing the image.
     * @param name
     *            the path to th eimage within the plugin.
     * @return the {@link ImageDescriptor} stored in the file at the specified path.
     * 
     * @deprecated Use {@link #getPluginImageDescriptor(String, String)} instead.
     */
    @Deprecated
    public static ImageDescriptor getPluginImageDescriptor(final Object plugin, final String name) {
        try {
            try {
                final URL url = ResourceManager.getPluginImageURL(plugin, name);
                return ImageDescriptor.createFromURL(url);
            } catch (final Throwable e) {
                // Ignore any exceptions
            }
        } catch (final Throwable e) {
            // Ignore any exceptions
        }
        return null;
    }

    /**
     * Returns an {@link ImageDescriptor} based on a {@link Bundle} and resource entry path.
     * 
     * @param symbolicName
     *            the symbolic name of the {@link Bundle}.
     * @param path
     *            the path of the resource entry.
     * @return the {@link ImageDescriptor} based on a {@link Bundle} and resource entry path.
     */
    public static ImageDescriptor getPluginImageDescriptor(final String symbolicName, final String path) {
        try {
            final URL url = ResourceManager.getPluginImageURL(symbolicName, path);
            if (url != null) {
                return ImageDescriptor.createFromURL(url);
            }
        } catch (final Throwable e) {
            // Ignore any exceptions
        }
        return null;
    }

    /**
     * Returns an {@link Image} based on given {@link URL}.
     */
    private static Image getPluginImageFromUrl(final URL url) {
        try {
            try {
                final String key = url.toExternalForm();
                Image image = ResourceManager.m_URLImageMap.get(key);
                if (image == null) {
                    final InputStream stream = url.openStream();
                    try {
                        image = SWTResourceManager.getImage(stream);
                        ResourceManager.m_URLImageMap.put(key, image);
                    } finally {
                        stream.close();
                    }
                }
                return image;
            } catch (final Throwable e) {
                // Ignore any exceptions
            }
        } catch (final Throwable e) {
            // Ignore any exceptions
        }
        return null;
    }

    /**
     * Returns an {@link URL} based on a plugin and file path.
     * 
     * @param plugin
     *            the plugin {@link Object} containing the file path.
     * @param name
     *            the file path.
     * @return the {@link URL} representing the file at the specified path.
     * @throws Exception
     */
    private static URL getPluginImageURL(final Object plugin, final String name) throws Exception {
        // try to work with 'plugin' as with OSGI BundleContext
        try {
            final Class<?> BundleClass = Class.forName("org.osgi.framework.Bundle"); //$NON-NLS-1$
            final Class<?> BundleContextClass = Class.forName("org.osgi.framework.BundleContext"); //$NON-NLS-1$
            if (BundleContextClass.isAssignableFrom(plugin.getClass())) {
                final Method getBundleMethod = BundleContextClass.getMethod("getBundle", new Class[0]); //$NON-NLS-1$
                final Object bundle = getBundleMethod.invoke(plugin, new Object[0]);
                //
                final Class<?> PathClass = Class.forName("org.eclipse.core.runtime.Path"); //$NON-NLS-1$
                final Constructor<?> pathConstructor = PathClass.getConstructor(new Class[] { String.class });
                final Object path = pathConstructor.newInstance(new Object[] { name });
                //
                final Class<?> IPathClass = Class.forName("org.eclipse.core.runtime.IPath"); //$NON-NLS-1$
                final Class<?> PlatformClass = Class.forName("org.eclipse.core.runtime.Platform"); //$NON-NLS-1$
                final Method findMethod = PlatformClass.getMethod("find", new Class[] { BundleClass, IPathClass }); //$NON-NLS-1$
                return (URL) findMethod.invoke(null, new Object[] { bundle, path });
            }
        } catch (final Throwable e) {
            // Ignore any exceptions
        }
        // else work with 'plugin' as with usual Eclipse plugin
        {
            final Class<?> PluginClass = Class.forName("org.eclipse.core.runtime.Plugin"); //$NON-NLS-1$
            if (PluginClass.isAssignableFrom(plugin.getClass())) {
                //
                final Class<?> PathClass = Class.forName("org.eclipse.core.runtime.Path"); //$NON-NLS-1$
                final Constructor<?> pathConstructor = PathClass.getConstructor(new Class[] { String.class });
                final Object path = pathConstructor.newInstance(new Object[] { name });
                //
                final Class<?> IPathClass = Class.forName("org.eclipse.core.runtime.IPath"); //$NON-NLS-1$
                final Method findMethod = PluginClass.getMethod("find", new Class[] { IPathClass }); //$NON-NLS-1$
                return (URL) findMethod.invoke(plugin, new Object[] { path });
            }
        }
        return null;
    }

    /**
     * Returns an {@link URL} based on a {@link Bundle} and resource entry path.
     */
    private static URL getPluginImageURL(final String symbolicName, final String path) {
        // try runtime plugins
        {
            final Bundle bundle = Platform.getBundle(symbolicName);
            if (bundle != null) {
                return bundle.getEntry(path);
            }
        }
        // try design time provider
        if (ResourceManager.m_designTimePluginResourceProvider != null) {
            return ResourceManager.m_designTimePluginResourceProvider.getEntry(symbolicName, path);
        }
        // no such resource
        return null;
    }
}