/**
 *
 */
package org.smapgen.sdm.map;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.smapgen.scl.exception.ClassLoaderException;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class MapperClassFields {

    public static StringBuffer mapperFields(final MappingField[] mapSource, final HashMap<String, MappingField> maptarget, final String sourceName, final String targetName) throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, Throwable, ClassLoaderException {

        final StringBuffer b = new StringBuffer();
        for (final MappingField sourceField : mapSource) {
            if (maptarget.containsKey(sourceField.getName())) {
                final MappingField targetField = maptarget.get(sourceField.getName());
                if(!targetField.getMapped()) {
                	b.append(DoMap.mapSourceIntoTarget(sourceName, targetName, sourceField, targetField));
                	targetField.setMapped(Boolean.TRUE);
                }
                sourceField.setMapped(Boolean.TRUE);
            }
        }
        return b;
    }
}
