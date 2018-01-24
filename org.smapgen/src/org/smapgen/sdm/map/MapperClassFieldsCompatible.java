/**
 *
 */
package org.smapgen.sdm.map;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.smapgen.scl.exception.ClassLoaderException;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MapperClassFieldsCompatible {

    /**
     * @param mapSource
     * @param maptarget
     * @param sourceName
     * @param targetName
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws Throwable
     * @throws ClassLoaderException
     */
    public static StringBuffer mapperFieldsCompatible(final MappingField[] mapSource, final HashMap<String, MappingField> maptarget, final String sourceName, final String targetName) throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, Throwable, ClassLoaderException {
        final StringBuffer b = new StringBuffer();
        for (final MappingField sourceField : mapSource) {
            for (final MappingField targetField : maptarget.values()) {
                if (!targetField.getMapped() && !sourceField.getMapped() && Utils.isCompatibleName(targetField.getName(), sourceField.getName())) {
                    b.append(DoMap.mapSourceIntoTarget(sourceName, targetName, sourceField, targetField));
                    targetField.setMapped(Boolean.TRUE);
                    sourceField.setMapped(Boolean.TRUE);
                }

            }
        }
        return b;
    }

    /**
     *
     */
    private MapperClassFieldsCompatible() {
        super();
    }

}
