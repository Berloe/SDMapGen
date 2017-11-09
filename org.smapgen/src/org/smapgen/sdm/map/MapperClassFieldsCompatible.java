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
    public static StringBuffer mapperFieldsCompatible( final  MappingField[] mapSource, final  HashMap<String, MappingField> maptarget,
            final String sourceName, final String targetName)
            throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException, Throwable, ClassLoaderException {
        StringBuffer b = new StringBuffer();
        for (MappingField sourceField : mapSource) {
            for (MappingField targetField : maptarget.values()) {
                if (!targetField.getMapped() && !sourceField.getMapped() && isNameCompatible(targetField, sourceField)) {
                    b.append(DoMap.mapSourceIntoTarget( sourceName, targetName, sourceField, targetField));
                    targetField.setMapped(Boolean.TRUE);
                    sourceField.setMapped(Boolean.TRUE);
                }

            }
        }
        return b;
    }
    /**
     * @param targetField
     * @param sourceField
     * @return
     */
    private static Boolean isNameCompatible(MappingField targetField, MappingField sourceField) {
        return targetField.getName().startsWith(sourceField.getName())
                || targetField.getName().endsWith(sourceField.getName())
                || sourceField.getName().startsWith(targetField.getName())
                || sourceField.getName().endsWith(targetField.getName());
    }
    /**
     * 
     */
    private MapperClassFieldsCompatible() {
        super();
    }

}
