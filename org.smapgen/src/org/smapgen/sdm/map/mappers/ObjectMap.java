package org.smapgen.sdm.map.mappers;

import org.smapgen.dconf.Dconf;
import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ObjectMap extends Mapper implements IMapper {

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(final MappingField sourceField, final MappingField targetField) {
        return MappingType.OBJECT.equals(sourceField.getGetterGenericType()) && (MappingType.OBJECT.equals(targetField.getSetterGenericType()) || Utils.isAbstract(sourceField.getFieldType()));
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName, final MappingField sourceField, final MappingField targetField) throws Throwable {
        final String newSourceName = Common.genName(sourceField.getFieldType(), Boolean.TRUE);
        final String objMapCode = Mapper.objectMapping(targetField, targetField.getFieldType(), targetName, sourceField, sourceField.getFieldType(), sourceField.getCalculatedFieldType(), newSourceName).toString();
        return Common.objMap(sourceField, newSourceName, objMapCode, Dconf.getInstance().containsNotNullAnot(targetField.getAnotations()));
    }

}
