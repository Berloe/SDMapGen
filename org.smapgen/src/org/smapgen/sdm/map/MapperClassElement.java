/**
 *
 */
package org.smapgen.sdm.map;

import java.util.HashMap;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MapperClassElement {

    /**
     *
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
    public static StringBuffer mapperInstance(final Object source, final Object target, final String sourceName, final String targetName) throws Throwable {

        final MappingField[] mapSource = Utils.getSourceMappinField(source, sourceName);
        final HashMap<String, MappingField> maptarget = Utils.getTargetMappingField(target, targetName);
        StringBuffer b = new StringBuffer();
        b = MapperClassFields.mapperFields(mapSource, maptarget, sourceName, targetName);

        b.append(MapperClassFieldsCompatible.mapperFieldsCompatible(mapSource, maptarget, sourceName, targetName));

        Common.setNotMappedNull(b, targetName, maptarget);

        return b;
    }

    /**
     *
     */
    private MapperClassElement() {
        super();
    }

}
