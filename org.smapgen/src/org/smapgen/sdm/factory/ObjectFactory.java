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
public class ObjectFactory {

    /**
     * .
     * 
     * @return void
     */
    public ObjectFactory() {}

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
    public static Object loader(final Class<?> classTarget)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, ClassLoaderException, NoSuchMethodException, SecurityException {
        Constructor<?> defConstructor = null;
        Object $ = null;

        try {
            defConstructor = classTarget.getConstructor();
        } catch (final NoSuchMethodException e) {

        } catch (final SecurityException e) {

        }
        if (defConstructor == null) {
            final Constructor<?>[] targetLoad = classTarget.getConstructors();
            for (final Constructor<?> constructor : targetLoad) {
                // If the constructor has no parameter, it is the default constructor
                if (constructor.getParameterTypes().length == 0) {
                    defConstructor = constructor;
                }
            }
        }
        // Verificamos si estamos ante un array

        if (defConstructor == null && classTarget.isArray()) {
            return Array.newInstance(classTarget.getComponentType(), 0);
        }

        if (defConstructor != null) {
            $ = defConstructor.newInstance(new Object[0]);
            try {
                $.getClass().getMethods();
                return $;
            } catch (final NoClassDefFoundError e) {
                String classname = e.getMessage().replace("/", ".");
                if (classname.startsWith("[L")) {
                    classname = classname.replace("[L", "");
                }
                if (classname.endsWith(";")) {
                    classname = classname.replace(";", "");
                }
                try {
                    ((SimpleClassLoader) classTarget.getClassLoader()).loadClassByName(classname);
                } catch (final Exception e2) {
                    throw e;
                }
                return loader($.getClass());
            }
        }

        return null;
    }

}