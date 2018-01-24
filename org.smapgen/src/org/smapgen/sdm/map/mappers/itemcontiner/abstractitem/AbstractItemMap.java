package org.smapgen.sdm.map.mappers.itemcontiner.abstractitem;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.map.mappers.instantiable.InstanceOfMap;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class AbstractItemMap extends InstanceOfMap implements IMapper {

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(final MappingField sourceField, final MappingField targetField) {
        return Utils.isAbstract(sourceField.getFieldType()) && MappingType.OBJECT.equals(sourceField.getGetterGenericType());
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName, final MappingField sourceField, final MappingField targetField) throws Throwable {
        final StringBuffer buffer = new StringBuffer();
        final String objMapCode = Mapper.objectMapping(targetField, targetField.getFieldType(), targetName, sourceField, sourceField.getFieldType(), sourceField.getFieldType(), sourceName).toString();
        Common.mappingObj(sourceName, objMapCode, buffer, true);
        return buffer;
    }
}
