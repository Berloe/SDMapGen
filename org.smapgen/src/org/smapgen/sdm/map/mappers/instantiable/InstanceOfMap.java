package org.smapgen.sdm.map.mappers.instantiable;

import java.lang.reflect.InvocationTargetException;

import org.smapgen.dconf.Dconf;
import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.MapperClassElement;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public abstract class InstanceOfMap extends Mapper {
    /**
     * @param sourceField
     * @param targetField
     * @param sourceName
     * @param newSourceName
     * @param targetName
     * @param excluded
     * @return
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws Throwable
     */
    protected StringBuffer instanceOfMapping(final MappingField sourceField, final MappingField targetField, final String sourceName, final String newSourceName, final String targetName, final Class<?>[] excluded) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, Throwable {

        final String sourceClass = sourceField.getFieldType().getCanonicalName();
        final String classExcluded = Common.excludeElements(sourceField.getFieldType(), sourceName, excluded).toString();
        StringBuffer objecMapping = new StringBuffer();
        if (classExcluded.length() > 0) {
            objecMapping = MapperClassElement.mapperInstance(ObjectFactory.loader(sourceField.getFieldType()), ObjectFactory.loader(targetField.getFieldType()), newSourceName, targetName);
        } else {
            objecMapping = Mapper.objectMapping(targetField, targetField.getFieldType(), targetName, sourceField, sourceField.getFieldType(), sourceField.getFieldType(), newSourceName);
        }

        return Common.instanceOfMap(sourceName, newSourceName, sourceClass, classExcluded, objecMapping.toString(), Dconf.getInstance().containsNotNullAnot(targetField.getAnotations()));
    }

}