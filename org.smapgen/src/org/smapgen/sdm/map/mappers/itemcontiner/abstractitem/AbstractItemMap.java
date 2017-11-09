package org.smapgen.sdm.map.mappers.itemcontiner.abstractitem;

import org.smapgen.sdm.common.Common;
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
        StringBuffer buffer = new StringBuffer();
        String objMapCode = objectMapping(targetField,targetField.getFieldType(),targetName,sourceField,sourceField.getFieldType(),sourceField.getFieldType(),sourceName).toString();
        Common.mappingObj(sourceName, objMapCode,buffer, true);
        return buffer;
    }
}
