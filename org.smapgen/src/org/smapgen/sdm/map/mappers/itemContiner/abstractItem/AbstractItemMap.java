package org.smapgen.sdm.map.mappers.itemcontiner.abstractitem;

import java.lang.reflect.InvocationTargetException;

import org.smapgen.scl.exception.ClassLoaderException;
import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.instantiable.InstanceOfMap;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class AbstractItemMap extends InstanceOfMap implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        return Utils.isAbstract(sourceField.getFieldType()) && MappingType.OBJECT.equals(sourceField.getGetterGenericType());
    }
    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName,MappingField sourceField, MappingField targetField) throws Throwable {
        Class<?>[] concreteClasses = Utils.getConcreteClasses(!sourceField.getFieldType().isArray() ? sourceField.getFieldType()
                : sourceField.getFieldType().getComponentType());
        StringBuffer buffer = new StringBuffer();
        if (concreteClasses != null && concreteClasses.length > 0) {
            for (int i = 0; i < concreteClasses.length; ++i) {
                Class<?> concreteSource = concreteClasses[i];
                Class<?> concreteTarget = Utils.findAbstractTarget(concreteSource.getSimpleName(),
                        !targetField.getFieldType().isArray() ? targetField.getFieldType()
                                : targetField.getFieldType().getComponentType());
                if (concreteTarget != null) {
                    mapResolvedClass(sourceName, targetName, sourceField.cloneMappingField(), targetField.cloneMappingField(), buffer, concreteSource,
                            concreteTarget);
                }
            }
        }
        return buffer;
    }
    /**
     * @param sourceName
     * @param targetName
     * @param sourceField
     * @param targetField
     * @param buffer
     * @param concreteSource
     * @param concreteTarget
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws ClassLoaderException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws Throwable
     */
    private void mapResolvedClass(final String sourceName, final String targetName, MappingField sourceField,
            MappingField targetField, StringBuffer buffer, Class<?> concreteSource, Class<?> concreteTarget)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, ClassLoaderException, NoSuchMethodException, SecurityException, Throwable {
        sourceField.setFieldType(concreteSource);
        targetField.setFieldType(concreteTarget);
        
        Object datoSource = ObjectFactory.loader(sourceField.getFieldType());

        final String newSourceName = Common.genName(datoSource, Boolean.FALSE);

        buffer.append(instanceOfMapping(sourceField, targetField, sourceName, newSourceName, targetName,  new Class<?>[] {}));
    }
    
  
}
