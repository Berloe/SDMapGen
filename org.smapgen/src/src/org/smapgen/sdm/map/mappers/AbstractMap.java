package org.smapgen.sdm.map.mappers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smapgen.dconf.Dconf;
import org.smapgen.scl.exception.ClassLoaderException;
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
public class AbstractMap extends Mapper implements IMapper {

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
        final StringBuffer auxSb = new StringBuffer();
        String newSourceName =  Common.createVar(auxSb, sourceField, sourceField.getFieldType());
        auxSb.append(mapAbstract(newSourceName, targetName, sourceField, targetField));
        return auxSb;
    }
    
    
    
    /**
     * @param sourceName
     * @param targetName
     * @param sourceField
     * @param targetField
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws Throwable
     */
    private StringBuffer mapAbstract( final String sourceName, final String targetName, MappingField sourceField, MappingField targetField)
            throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException, Throwable {
        Class<?>[] concreteClasses = Utils.getConcreteClasses(!sourceField.getFieldType().isArray() ? sourceField.getFieldType()
                : sourceField.getFieldType().getComponentType());
        StringBuffer b = new StringBuffer();
        if (concreteClasses != null && concreteClasses.length > 0) {
            for (int i = 0; i < concreteClasses.length; ++i) {
                Class<?> concreteSource = concreteClasses[i];
                Class<?> concreteTarget = Utils.findAbstractTarget(concreteSource.getSimpleName(),
                        !targetField.getFieldType().isArray() ? targetField.getFieldType()
                                : targetField.getFieldType().getComponentType());
                if (concreteTarget != null) {
                    b = mapResolvedClass(sourceName, targetName, sourceField, targetField, concreteSource,
                            concreteTarget);
                }
            }
        }
        return b;
    }
    /**
     * @param sourceName
     * @param targetName
     * @param sourceField
     * @param targetField
     * @param concreteSource
     * @param concreteTarget
     * @return
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws ClassLoaderException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws Throwable
     */
    private StringBuffer mapResolvedClass(final String sourceName, final String targetName, MappingField sourceField,
            MappingField targetField, Class<?> concreteSource, Class<?> concreteTarget)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, ClassLoaderException, NoSuchMethodException, SecurityException, Throwable {
        sourceField.setFieldType(concreteSource);
        targetField.setFieldType(concreteTarget);

        return doMap( sourceName, targetName, sourceField, targetField);
    }
    /**
     * @param b
     * @param sourceName
     * @param targetName
     * @param sourceField
     * @param targetField
     * @param st 
     * @return 
     * @throws ClassLoaderException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    private StringBuffer doMap( final String sourceName, final String targetName, MappingField sourceField,
            MappingField targetField) throws Throwable{
        List<IMapper> mops = Dconf.getInstance().getMapperAbs();
        StringBuffer b = new StringBuffer();
        for (IMapper mapperOps : mops) {
            if (mapperOps.isAplicable(sourceField, targetField)){
                return mapperOps.map( sourceName, targetName, sourceField, targetField);
            }
        }
        return b;
    }
}
