package org.smapgen.sdm.map.mappers;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ObjectMap extends Mapper implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        return MappingType.OBJECT.equals(sourceField.getGetterGenericType()) && MappingType.OBJECT.equals(targetField.getSetterGenericType());
    }
    
    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName,MappingField sourceField, MappingField targetField) throws Throwable {
        final String newSourceName = Common.genName(sourceField.getFieldType(), Boolean.TRUE);
        String objMapCode = objectMapping(targetField,targetField.getFieldType(),targetName,sourceField,sourceField.getFieldType(),ObjectFactory.loader(sourceField.getCalculatedFieldType()),newSourceName).toString();
        return Common.objMap(sourceField, newSourceName, objMapCode);
    }
    
}
