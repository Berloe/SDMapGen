/**
 *
 */
package org.smapgen.sdm.map.mappers.common;

import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public interface IMapper {
    /**
     * @param sourceField
     * @param targetField
     * @return
     */
    Boolean isAplicable(MappingField sourceField, MappingField targetField);

    /**
     * @param sourceName
     * @param targetName
     * @param sourceField
     * @param targetField
     * @return
     * @throws Throwable
     */
    StringBuffer map(String sourceName, String targetName, MappingField sourceField, MappingField targetField) throws Throwable;
}
