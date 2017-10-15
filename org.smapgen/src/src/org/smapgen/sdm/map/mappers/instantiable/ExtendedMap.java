package org.smapgen.sdm.map.mappers.instantiable;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ExtendedMap extends InstanceOfMap implements IMapper {
    
    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(String sourceName, String targetName, MappingField sourceField,
            MappingField targetField) throws Throwable {
        String sourceCalculatedType = sourceField.getCalculatedFieldType().isArray()?sourceField.getField().getType().getComponentType().getSimpleName():sourceField.getCalculatedFieldType().getSimpleName();
        Class<?>[] excluded = sourceCalculatedType.equals(sourceField.getFieldType().getSimpleName())
                ? Utils.getConcreteClasses(sourceField.getFieldType()) : new Class<?>[] {};
        Object datoSource = ObjectFactory.loader(sourceField.getFieldType()/*.getResolvedAbsClss()*/);

        final String newSourceName = excluded.length>0?sourceName:Common.genName(datoSource, Boolean.FALSE);

        return instanceOfMapping(sourceField, targetField, sourceName, newSourceName, targetName, excluded);

    }

    /* (non-Javadoc)
     * @see org.sdm.map.IMapper#isAplicable(org.sdm.metadata.MappingField, org.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {

        try {
            return !Utils.isAbstract(sourceField.getFieldType()) && Utils.getConcreteClasses(sourceField.getFieldType()).length>1;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
