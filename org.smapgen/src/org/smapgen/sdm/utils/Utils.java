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
import org.smapgen.sdm.ISimpleDataObjMapper;
import org.smapgen.sdm.metadata.FieldUtils;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class Utils implements ISimpleDataObjMapper {
    private static final char DOT_CHAR = ".".charAt(0);
    private static final char SLASH_CHAR = "/".charAt(0);

    /**
     * @param simpleName
     * @param targetClass
     * @return
     * @throws Throwable
     */
    public static Class<?> findAbstractTarget(final String simpleName, final Class<?> targetClass) throws Throwable {
        if (!Utils.isAbstract(targetClass)) {
            if (simpleName.equals(targetClass.getSimpleName())) {
                return targetClass;
            }
        } else {
            final Class<?>[] concreteClasses = Utils.getConcreteClasses(targetClass);
            if (concreteClasses != null && concreteClasses.length > 0) {
                for (final Class<?> concreteClasse : concreteClasses) {
                    if (simpleName.equals(concreteClasse.getSimpleName())) {
                        return concreteClasse;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param clazz
     * @return
     * @throws Throwable
     */
    public static Class<?>[] getConcreteClasses(final Class<?> clazz) throws Throwable {
        final SimpleClassLoader scl = (SimpleClassLoader) clazz.getClassLoader();
        if (scl == null) {
            return new Class<?>[] { clazz };
        }
        final Set<String> classesRouted = scl.getClassMap().keySet();
        final ArrayList<String> depsList = new ArrayList<>(classesRouted);
        Boolean loadDeps = Boolean.FALSE;
        for (final String dep : depsList) {
            if (!scl.getClassMap().containsKey(dep)) {
                loadDeps = Boolean.TRUE;
            }
        }
        if (loadDeps) {
            scl.loadDeps(depsList);
            for (final String dep : depsList) {
                scl.loadClassByName(dep);
            }
        }
        return Utils.shearchImplClasses(clazz, scl);
    }

    /**
     *
     * @param MappingField
     *            targetMappingField.
     * @return String
     */
    public static String getConcreteType(final MappingField targetMappingField) {
        return (targetMappingField.getSetterMethod().getReturnType().getName().equals(Collection.class.getName()) ? ArrayList.class : targetMappingField.getSetterMethod().getReturnType().getName().equals(Set.class.getName()) ? HashSet.class : !targetMappingField.getSetterMethod().getReturnType().getName().equals(List.class.getName()) && targetMappingField.getSetterMethod().getReturnType().getName().equals(SortedSet.class.getName()) ? TreeSet.class : ArrayList.class).getCanonicalName();
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
     * @param objClass
     * @return
     */
    public static boolean isAbstract(final Class<?> objClass) {
        return Modifier.isAbstract(objClass.getModifiers()) || Modifier.isInterface(objClass.getModifiers());
    }

    public static boolean isCompatibleName(final String simpleName, final String simpleName2) {
        return simpleName.equals(simpleName2) || simpleName.startsWith(simpleName2) || simpleName.endsWith(simpleName2) || simpleName2.startsWith(simpleName) || simpleName2.endsWith(simpleName) || Utils.matchPercent(simpleName, simpleName2);
    }

    /**
     * @param auxSimpleName
     * @param auxsimpleName2
     * @return
     */
    public static boolean matchPercent(final String SimpleName, final String simpleName2) {
        final String auxSimpleName = SimpleName.toLowerCase();
        final String auxsimpleName2 = simpleName2.toLowerCase();
        int match = 0;
        int offset1 = 0;
        int offset2 = 0;
        if (auxSimpleName.charAt(0) != auxsimpleName2.charAt(0)) {
            final String init1 = String.valueOf(auxSimpleName.charAt(0));
            final String init2 = String.valueOf(auxsimpleName2.charAt(0));
            if (auxSimpleName.indexOf(init2) < auxsimpleName2.indexOf(init1)) {
                offset1 = auxSimpleName.indexOf(init2);
            } else {
                offset2 = auxsimpleName2.indexOf(init1);
            }
        }
        if (offset1 < 0 || offset2 < 0) {
            return false;
        }
        final int length = auxSimpleName.length() > auxsimpleName2.length() ? auxsimpleName2.length() : auxSimpleName.length();
        for (int i = 0; i + offset1 + offset2 < length; i++) {
            if (auxSimpleName.charAt(i + offset1) == auxsimpleName2.charAt(i + offset2)) {
                ++match;
            }
        }
        final double result = match * 100 / length;
        if (result > ISimpleDataObjMapper.config.getCompatThreshold().intValue()) {
            return true;
        }
        return false;
    }

    /**
     * @param clazz
     * @param retrnClasses
     * @param scl
     * @return
     * @throws Throwable
     */
    private static Class<?>[] shearchImplClasses(final Class<?> clazz, final SimpleClassLoader scl) throws Throwable {
        final Map<String, Class<?>> retrnClasses = new HashMap<>();
        final Class<?>[] auxClasses = scl.getClassCacheValues();
        for (int i = 0; i < auxClasses.length; ++i) {
            if (!Utils.isAbstract(auxClasses[i]) && clazz.isAssignableFrom(auxClasses[i])) {
                retrnClasses.put(auxClasses[i].getCanonicalName(), auxClasses[i]);
            } else if (Utils.isAbstract(auxClasses[i]) && clazz.isAssignableFrom(auxClasses[i]) && !clazz.equals(auxClasses[i])) {
                final Class<?>[] temClassArr = Utils.getConcreteClasses(auxClasses[i]);
                if (temClassArr != null && temClassArr.length > 0) {
                    for (final Class<?> class1 : temClassArr) {
                        retrnClasses.put(class1.getCanonicalName(), class1);
                    }
                }
            }
        }
        if (retrnClasses.size() <= 0) {
            final String[] classesNames = scl.getLoadedClassNames();
            for (final String cl : classesNames) {
                final String name = cl.replace(Utils.SLASH_CHAR, Utils.DOT_CHAR);
                if (name.lastIndexOf(Utils.DOT_CHAR) > 0 && clazz.getCanonicalName().lastIndexOf(Utils.DOT_CHAR) > 0 && name.substring(0, name.lastIndexOf(Utils.DOT_CHAR)).equals(clazz.getCanonicalName().substring(0, clazz.getCanonicalName().lastIndexOf(Utils.DOT_CHAR)))) {
                    try {
                        final Class<?> sclass = scl.loadClass(cl.replace(Utils.SLASH_CHAR, Utils.DOT_CHAR));
                        if (!Utils.isAbstract(sclass) && clazz.isAssignableFrom(sclass)) {
                            retrnClasses.put(sclass.getCanonicalName(), sclass);
                        } else if (Utils.isAbstract(sclass) && clazz.isAssignableFrom(sclass) && !clazz.equals(sclass)) {
                            final Class<?>[] temClassArr = Utils.getConcreteClasses(sclass);
                            if (temClassArr != null && temClassArr.length > 0) {
                                for (final Class<?> class1 : temClassArr) {
                                    retrnClasses.put(class1.getCanonicalName(), class1);
                                }
                            }
                        }
                    } catch (final ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return retrnClasses.values().toArray(new Class<?>[0]);
    }

    /**
     *
     */
    private Utils() {
        super();
    }
}
