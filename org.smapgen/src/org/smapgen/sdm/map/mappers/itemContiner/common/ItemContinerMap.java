package org.smapgen.sdm.map.mappers.itemContiner.common;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.map.mappers.itemContiner.abstractItem.AbstractItemMap;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ItemContinerMap extends Mapper {

    /**
     * @param sourceField
     * @param targetField
     * @param sourceName
     * @param targetName
     * @return
     * @throws Throwable
     */
    public StringBuffer mapItemElement(final MappingField sourceField, final MappingField targetField,
            final String sourceName, final String targetName) throws Throwable {
        final StringBuffer buffer = new StringBuffer();
        final String newTargetName;
        if (Utils.isAbstract(targetField.getFieldType())) {
            newTargetName = Common.createNewVarNull(buffer, targetField.getFieldType());
            buffer.append((new AbstractItemMap()).map(sourceName, newTargetName, sourceField, targetField));
        } else {
            newTargetName = Common.createNewVar(buffer, ObjectFactory.loader(targetField.getFieldType()));

            buffer.append(objectMapping(targetField, targetField.getFieldType(), newTargetName, sourceField,
                    sourceField.getFieldType(), sourceField.getFieldType(), sourceName));
        }
        buffer.append("list").append(targetName).append(".add(").append(newTargetName).append(");");
        return buffer;
    }

}