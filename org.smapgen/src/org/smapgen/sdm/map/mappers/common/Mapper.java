package org.smapgen.sdm.map.mappers.common;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.smapgen.scl.exception.ClassLoaderException;
import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.common.ConstantValues;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.MapperClassElement;
import org.smapgen.sdm.map.mappers.instantiable.ExtendedMap;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.registry.Registry;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public abstract class Mapper {

    /**
     * @param targetMappingField
     * @param targetClass
     * @param targetName
     * @param sourceMappingField
     * @param sourceClass
     * @param computeSrc
     * @param newSourceName
     * @return
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws Throwable
     */
    public static StringBuffer objectMapping(final MappingField targetMappingField, final Class<?> targetClass, final String targetName, final MappingField sourceMappingField, final Class<?> sourceClass, final Class<?> computeSrc, final String newSourceName) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, Throwable {
        final StringBuffer b = new StringBuffer();

        if (!Mapper.checkFunctionDefined(targetMappingField, targetClass, targetName, sourceClass, newSourceName, b)) {
            final StringBuffer fcode = new StringBuffer();
            final String newtargetName = Utils.isAbstract(targetClass) ? Common.createNewVarNull(fcode, targetClass) : Common.createNewVar(fcode, targetClass);
            final String fName = Registry.registerName(ConstantValues.ClassMapper_mapPrefix + sourceClass.getSimpleName());
            Registry.registreFunction(sourceClass.getCanonicalName(), targetClass.getCanonicalName(), fName);
            Mapper.mapInnerObject(targetMappingField, targetClass, sourceMappingField, computeSrc, newSourceName, fcode, newtargetName);

            Common.addMappingMethod(fcode, computeSrc, targetClass, newtargetName, newSourceName, fName, true);
            b.append(targetMappingField.getSetterMethod().getParameterTypes()[0].isAssignableFrom(targetClass) && targetMappingField.getVarName().equals(targetName) ? Common.valueAssignFunction(targetMappingField, newSourceName, fName) : Common.valueAssignFunction(targetName, newSourceName, fName));
        }
        return b;
    }

    /**
     * @param targetMappingField
     * @param targetClass
     * @param targetName
     * @param sourceClass
     * @param newSourceName
     * @param b
     * @param funtionSeted
     * @return
     */
    private static boolean checkFunctionDefined(final MappingField targetMappingField, final Class<?> targetClass, final String targetName, final Class<?> sourceClass, final String newSourceName, final StringBuffer b) {
        boolean funtionSeted = false;
        if (Registry.containsFuncReturn(targetClass.getCanonicalName())) {
            final HashMap<String, String> funtion = Registry.getFunctionsRegistry(targetClass.getCanonicalName());
            if (funtion.containsKey(sourceClass.getCanonicalName())) {
                final String fName = funtion.get(sourceClass.getCanonicalName());
                b.append(targetMappingField.getSetterMethod().getParameterTypes()[0].isAssignableFrom(targetClass) && targetMappingField.getVarName().equals(targetName) ? Common.valueAssignFunction(targetMappingField, newSourceName, fName) : Common.valueAssignFunction(targetName, newSourceName, fName));
                funtionSeted = true;
            }
        }
        return funtionSeted;
    }

    /**
     * @param targetMappingField
     * @param targetClass
     * @param sourceMappingField
     * @param computeSrc
     * @param newSourceName
     * @param fcode
     * @param newtargetName
     * @throws Throwable
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws ClassLoaderException
     * @throws NoSuchMethodException
     */
    private static void mapInnerObject(final MappingField targetMappingField, final Class<?> targetClass, final MappingField sourceMappingField, final Class<?> computeSrc, final String newSourceName, final StringBuffer fcode, final String newtargetName) throws Throwable, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, ClassLoaderException, NoSuchMethodException {
        if (Utils.getConcreteClasses(computeSrc).length == 1 && !Utils.isAbstract(computeSrc) && !Utils.isAbstract(targetClass)) {
            fcode.append(MapperClassElement.mapperInstance(ObjectFactory.loader(computeSrc), ObjectFactory.loader(targetClass), newSourceName, newtargetName));
        } else {
            fcode.append(Mapper.mapperClass(computeSrc, targetClass, newSourceName, newtargetName, sourceMappingField, targetMappingField));
        }
    }

    /**
     *
     * @param sourceField
     * @param targetField
     * @param StringBuffer
     *            sb.
     * @param Object
     *            source
     * @param Object
     *            target
     * @param String
     *            sourceName
     * @param String
     *            targetName
     * @return Boolean
     * @throws Throwable
     */
    private static StringBuffer mapperClass(final Class<?> source, final Class<?> target, final String sourceName, final String targetName, final MappingField sourceField, final MappingField targetField) throws Throwable {
        final StringBuffer b = new StringBuffer();
        final Class<?>[] classExtends = Utils.getConcreteClasses(source);
        for (final Class<?> concreteSource : classExtends) {
            final Class<?> targetExtends = Mapper.findExtensionsTarget(concreteSource.getSimpleName(), target);
            if (targetExtends != null) {

                Mapper.mapResolvedClass(sourceName, targetName, sourceField.cloneMappingField(), targetField.cloneMappingField(), b, concreteSource, targetExtends);
            }
        }
        return b;
    }

    /**
     * @param source
     * @param sourceName
     * @param targetName
     * @param sourceField
     * @param targetField
     * @param b
     * @param concreteSource
     * @param targetExtends
     * @throws Throwable
     */
    private static void mapResolvedClass(final String sourceName, final String targetName, final MappingField sourceField, final MappingField targetField, final StringBuffer b, final Class<?> concreteSource, final Class<?> targetExtends) throws Throwable {
        sourceField.setFieldType(concreteSource);
        targetField.setFieldType(targetExtends);

        b.append(new ExtendedMap().map(sourceName, targetName, sourceField, targetField));
    }

    /**
     * @param simpleName
     * @param targetClass
     * @return
     * @throws Throwable
     */
    protected static Class<?> findExtensionsTarget(final String simpleName, final Class<?> targetClass) throws Throwable {
        final Class<?>[] concreteClasses = Utils.getConcreteClasses(targetClass);
        if (concreteClasses != null && concreteClasses.length > 0) {
            for (final Class<?> concreteClasse : concreteClasses) {
                if (Utils.isCompatibleName(simpleName, concreteClasse.getSimpleName())) {
                    return concreteClasse;
                }
            }
        }
        return null;
    }
}
