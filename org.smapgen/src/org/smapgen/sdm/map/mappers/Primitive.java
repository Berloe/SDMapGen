package org.smapgen.sdm.map.mappers;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class Primitive extends Mapper implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        return sourceField.getField().getType().equals(targetField.getField().getType())
                && !isSourceComplextype(sourceField) && !isTargetComplextype(targetField);
    }
    /**
     * @param sourceField
     * @return
     */
    private Boolean isSourceComplextype(MappingField sourceField) {
        return MappingType.ARRAY.equals(sourceField.getGetterGenericType())
                || MappingType.COLLECTION.equals(sourceField.getGetterGenericType())
                || MappingType.MAP.equals(sourceField.getGetterGenericType());
    }

    /**
     * @param targetField
     * @return
     */
    private Boolean isTargetComplextype(MappingField targetField) {
        return MappingType.ARRAY.equals(targetField.getSetterGenericType())
                || MappingType.COLLECTION.equals(targetField.getSetterGenericType())
                || MappingType.MAP.equals(targetField.getSetterGenericType());
    }
    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName,MappingField sourceField, MappingField targetField) {
        return Common.valueAssign(sourceField, targetField);
    }
    
}
