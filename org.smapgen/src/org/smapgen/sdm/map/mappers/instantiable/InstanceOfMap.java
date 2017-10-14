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

    public InstanceOfMap() {
        super();
    }

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
    protected StringBuffer instanceOfMapping( MappingField sourceField, MappingField targetField,
            String sourceName,final String newSourceName,final String targetName, Class<?>[] excluded) throws IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException, ClassNotFoundException, Throwable {

        String sourceClass = sourceField.getFieldType().getCanonicalName();
        String classExcluded = Common.excludeElements(sourceField.getFieldType(), sourceName, excluded).toString();
        StringBuffer objecMapping = new StringBuffer();
        if(classExcluded.length()>0){
            objecMapping= (new MapperClassElement()).mapperInstance(ObjectFactory.loader(sourceField.getFieldType()), ObjectFactory.loader(targetField.getFieldType()), newSourceName, targetName);
                    }else{
            objecMapping = objectMapping(targetField,targetField.getFieldType(),targetName,sourceField,sourceField.getFieldType()/*.getResolvedAbsClss()*/,
                ObjectFactory.loader( sourceField.getFieldType()),newSourceName);
        }

        return Common.instanceOfMap( sourceName, newSourceName, sourceClass, classExcluded, objecMapping.toString(),Dconf.getInstance().containsNotNullAnot(targetField.getAnotations()));
    }

}