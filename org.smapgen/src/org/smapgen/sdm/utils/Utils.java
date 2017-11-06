package org.smapgen.sdm.utils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.smapgen.scl.SimpleClassLoader;
import org.smapgen.sdm.metadata.FieldUtils;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class Utils {

    /**
     * 
     */
    private Utils() {
        super();
    }


    /**
     * @param clazz
     * @return
     * @throws Throwable
     */
    public static Class<?>[] getConcreteClasses(Class<?> clazz) throws Throwable {
        Map<String, Class<?>> retrnClasses = new HashMap<String, Class<?>>();
        SimpleClassLoader scl = (SimpleClassLoader) clazz.getClassLoader();
        if (scl == null) {
            return new Class<?>[] { clazz };
        }
        Set<String> classesRouted = scl.getClassMap().keySet();
        ArrayList<String> depsList = new ArrayList<String>(classesRouted);
        Boolean loadDeps = Boolean.FALSE;
        for (String dep : depsList) {
            if (!scl.getClassMap().containsKey(dep)) {
                loadDeps = Boolean.TRUE;
            }
        }
        if (loadDeps) {
            scl.loadDeps(depsList);
            for (String dep : depsList) {
                scl.loadClassByName(dep);
            }
        }
        return shearchImplClasses(clazz, retrnClasses, scl);
    }


    /**
     * @param objClass
     * @return
     */
    public static boolean isAbstract(Class<?> objClass) {
        return Modifier.isAbstract(objClass.getModifiers()) || Modifier.isInterface(objClass.getModifiers());
    }

    /**
     * @param clazz
     * @param retrnClasses
     * @param scl
     * @return
     * @throws Throwable
     */
    private static Class<?>[] shearchImplClasses(Class<?> clazz, Map<String, Class<?>> retrnClasses, SimpleClassLoader scl)
            throws Throwable {
        Class<?>[] auxClasses = scl.getClassCacheValues();
        for (int i = 0; i < auxClasses.length; ++i) {
            if (!isAbstract(auxClasses[i]) && clazz.isAssignableFrom(auxClasses[i])) {
                retrnClasses.put(auxClasses[i].getCanonicalName(), auxClasses[i]);
            } else if (isAbstract(auxClasses[i]) && clazz.isAssignableFrom(auxClasses[i])
                    && !clazz.equals(auxClasses[i])) {
                Class<?>[] temClassArr = getConcreteClasses(auxClasses[i]);
                if (temClassArr != null && temClassArr.length > 0) {
                    for (Class<?> class1 : temClassArr) {
                        retrnClasses.put(class1.getCanonicalName(), class1);
                    }
                }
            }
        }
        if (retrnClasses.size() <= 0) {
            String[] classesNames = scl.getLoadedClassNames();
            for (int i = 0; i < classesNames.length; ++i) {
                String cl = classesNames[i];
                String name = cl.replace("/", ".");
                if (name.lastIndexOf(".") > 0 && clazz.getCanonicalName().lastIndexOf(".") > 0
                        && name.substring(0, name.lastIndexOf(".")).equals(
                                clazz.getCanonicalName().substring(0, clazz.getCanonicalName().lastIndexOf(".")))) {
                    try {
                        Class<?> sclass = scl.loadClass(cl.replace("/", "."));
                        if (!isAbstract(sclass) && clazz.isAssignableFrom(sclass)) {
                            retrnClasses.put(sclass.getCanonicalName(), sclass);
                        } else if (isAbstract(sclass) && clazz.isAssignableFrom(sclass) && !clazz.equals(sclass)) {
                            Class<?>[] temClassArr = getConcreteClasses(sclass);
                            if (temClassArr != null && temClassArr.length > 0) {
                                for (Class<?> class1 : temClassArr) {
                                    retrnClasses.put(class1.getCanonicalName(), class1);
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return retrnClasses.values().toArray(new Class<?>[0]);
    }
    /**
     * 
     * @param MappingField
     *            targetMappingField.
     * @return String
     */
    public static String getConcreteType(final MappingField targetMappingField) {
        return (targetMappingField.getSetterMethod().getReturnType().getName().equals(Collection.class.getName())
                ? ArrayList.class
                : targetMappingField.getSetterMethod().getReturnType().getName().equals(Set.class.getName())
                        ? HashSet.class
                        : !targetMappingField.getSetterMethod().getReturnType().getName().equals(List.class.getName())
                                && targetMappingField.getSetterMethod().getReturnType().getName()
                                        .equals(SortedSet.class.getName()) ? TreeSet.class : ArrayList.class)
                                                .getCanonicalName();
    }
    /**
     * @param source
     * @param sourceName
     * @return
     * @throws Exception
     */
    public static MappingField[] getSourceMappinField(final Object source, final String sourceName) throws Exception {
        return FieldUtils.fieldGetter(source.getClass(), sourceName);
    }
    
    /**
     * @param target
     * @param targetName
     * @return
     */
    public static HashMap<String, MappingField> getTargetMappingField(final Object target, final String targetName) {
        return FieldUtils.hashSetFieldLoader(target.getClass(), targetName);
    }

    /**
     * @param simpleName
     * @param targetClass
     * @return
     * @throws Throwable
     */
    public static Class<?> findAbstractTarget(String simpleName, Class<?> targetClass) throws Throwable {
        if (!Utils.isAbstract(targetClass)) {
            if (simpleName.equals(targetClass.getSimpleName())) {
                return targetClass;
            }
        } else {
            Class<?>[] concreteClasses = Utils.getConcreteClasses(targetClass);
            if (concreteClasses != null && concreteClasses.length > 0) {
                for (int i = 0; i < concreteClasses.length; ++i) {
                    if (simpleName.equals(concreteClasses[i].getSimpleName())) {
                        return concreteClasses[i];
                    }
                }
            }
        }
        return null;
    }
}
