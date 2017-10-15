package org.smapgen.sdm.map.mappers.common;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.common.ConstantValues;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.MapperClassElement;
import org.smapgen.sdm.map.mappers.instantiable.ExtendedMap;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.registry.Registry;
import org.smapgen.sdm.utils.Utils;;

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
     * @param datoSource
     * @param newSourceName
     * @return
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws Throwable
     */
    public StringBuffer objectMapping( MappingField targetMappingField,final Class<?> targetClass, final String targetName,
            MappingField sourceMappingField, final Class<?> sourceClass, final Object datoSource,final String newSourceName)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, Throwable {
        final StringBuffer b = new StringBuffer();
        boolean funtionSeted = false;
        if (Registry.containsFuncReturn(targetClass.getCanonicalName())) {
            final HashMap<String, String> funtion = Registry.getFunctionsRegistry(targetClass.getCanonicalName());
            if (funtion.containsKey(sourceClass.getCanonicalName())) {
                final String fName = funtion.get(sourceClass.getCanonicalName());
                b.append(targetMappingField.getSetterMethod().getParameterTypes()[0].isAssignableFrom(targetClass) &&  targetMappingField.getVarName().equals(targetName)
                        ? Common.valueAssignFunction(targetMappingField, newSourceName, fName)
                        : Common.valueAssignFunction(targetName, newSourceName, fName));
                funtionSeted = true;
            }
        }

        if (!funtionSeted) {
            StringBuffer fcode = new StringBuffer();
            String fName = null;
            String newtargetName = null;

            Object datoTarget = ObjectFactory.loader(targetClass);
            newtargetName = Common.createNewVar(fcode, datoTarget);
            fName = Registry.registerName(ConstantValues.ClassMapper_mapPrefix + sourceClass.getSimpleName());
            Registry.registreFunction(sourceClass.getCanonicalName(), targetClass.getCanonicalName(), fName);
            if (Utils.getConcreteClasses(datoSource.getClass()).length==1) {
                fcode.append((new MapperClassElement()).mapperInstance( datoSource, datoTarget, newSourceName, newtargetName));
            } else {
                fcode.append(mapperClass(datoSource, datoTarget, newSourceName, newtargetName, sourceMappingField,
                        targetMappingField));
            }

            Common.addMappingMethod(fcode, datoSource.getClass(), targetClass, newtargetName, newSourceName, fName,
                    true);
            b.append(targetMappingField.getSetterMethod().getParameterTypes()[0].isAssignableFrom(targetClass)  && targetMappingField.getVarName().equals(targetName)
                    ? Common.valueAssignFunction(targetMappingField, newSourceName, fName)
                    : Common.valueAssignFunction(targetName, newSourceName, fName));
        }
        return b;
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
    private StringBuffer mapperClass( final Object source, final Object target, final String sourceName,
            final String targetName, MappingField sourceField, MappingField targetField) throws Throwable {
        StringBuffer b = new StringBuffer();
        Class<?>[] classExtends = Utils.getConcreteClasses(source.getClass());
            for (int i = 0; i < classExtends.length; ++i) {
                Class<?> concreteSource = classExtends[i];
                Class<?> targetExtends = findExtensionsTarget(concreteSource.getSimpleName(), target.getClass());
                if (targetExtends != null) {

                    mapResolvedClass(source, sourceName, targetName, sourceField.cloneMappingField(), targetField.cloneMappingField(), b, concreteSource,
                            targetExtends);
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
    private void mapResolvedClass(final Object source, final String sourceName, final String targetName,
            MappingField sourceField, MappingField targetField, StringBuffer b, Class<?> concreteSource,
            Class<?> targetExtends) throws Throwable {
        sourceField.setFieldType(concreteSource);
        targetField.setFieldType(targetExtends);

        b.append((new ExtendedMap()).map(sourceName, targetName, sourceField, targetField));
    }
    
    /**
     * @param simpleName
     * @param targetClass
     * @return
     * @throws Throwable
     */
    protected Class<?> findExtensionsTarget(String simpleName, Class<?> targetClass) throws Throwable {
        Class<?>[] concreteClasses = Utils.getConcreteClasses(targetClass);
        if (concreteClasses != null && concreteClasses.length > 0) {
            for (int i = 0; i < concreteClasses.length; ++i) {
                if (simpleName.equals(concreteClasses[i].getSimpleName())) {
                    return concreteClasses[i];
                }
            }
        }
        return null;
    }
    

    
}
