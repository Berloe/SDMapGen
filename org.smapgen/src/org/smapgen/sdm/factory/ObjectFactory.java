package org.smapgen.sdm.factory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.smapgen.scl.SimpleClassLoader;
import org.smapgen.scl.exception.ClassLoaderException;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class ObjectFactory {

    /**
     *
     * @param Class<?>
     *            classTarget
     * @return Object
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws ClassLoaderException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static Object loader(final Class<?> classTarget) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, ClassLoaderException, NoSuchMethodException {
        final Constructor<?> defConstructor = ObjectFactory.getConstructor(classTarget);
        // Verificamos si estamos ante un array

        if (defConstructor == null && classTarget.isArray()) {
            return Array.newInstance(classTarget.getComponentType(), 0);
        }

        if (defConstructor != null) {
            final Object $ = defConstructor.newInstance(new Object[0]);
            try {
                $.getClass().getMethods();
                return $;
            } catch (final NoClassDefFoundError e) {
                try {
                    final String classname = ObjectFactory.extractDependency(e);
                    ((SimpleClassLoader) classTarget.getClassLoader()).loadClassByName(classname);
                } catch (final Exception e2) {
                    throw e;
                }
                return ObjectFactory.loader($.getClass());
            }
        }

        return null;
    }

    /**
     * @param e
     * @return
     */
    private static String extractDependency(final NoClassDefFoundError e) {
        String classname = e.getMessage().replace("/", ".");
        if (classname.startsWith("[L")) {
            classname = classname.replace("[L", "");
        }
        if (classname.endsWith(";")) {
            classname = classname.replace(";", "");
        }
        return classname;
    }

    /**
     * @param classTarget
     * @param defConstructor
     * @return
     * @throws SecurityException
     */
    private static Constructor<?> getConstructor(final Class<?> classTarget) throws SecurityException {
        Constructor<?> defConstructor = null;
        try {
            defConstructor = classTarget.getConstructor();
        } catch (final NoSuchMethodException | SecurityException e) {}
        if (defConstructor == null) {
            final Constructor<?>[] targetLoad = classTarget.getConstructors();
            for (final Constructor<?> constructor : targetLoad) {
                // If the constructor has no parameter, it is the default constructor
                if (constructor.getParameterTypes().length == 0) {
                    defConstructor = constructor;
                }
            }
        }
        return defConstructor;
    }

    /**
     * .
     *
     * @return void
     */
    private ObjectFactory() {
        super();
    }

}