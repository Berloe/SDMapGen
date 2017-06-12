package org.smapgen.sdm.map.mappers;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class Primitive extends Mapper implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        if(targetField.getFieldType().equals(String.class)){
            if (sourceField.getFieldType().equals(char.class)||
                sourceField.getFieldType().equals(boolean.class)||
                sourceField.getFieldType().equals(char[].class)||
                sourceField.getFieldType().equals(double.class)||
                sourceField.getFieldType().equals(float.class)||
                sourceField.getFieldType().equals(int.class)||
                sourceField.getFieldType().equals(long.class)
                    ){
                return true;
            }else if(char.class.isAssignableFrom(sourceField.getFieldType())||
                    boolean.class.isAssignableFrom(sourceField.getFieldType())||
                    char[].class.isAssignableFrom(sourceField.getFieldType())||
                    double.class.isAssignableFrom(sourceField.getFieldType())||
                    float.class.isAssignableFrom(sourceField.getFieldType())||
                    int.class.isAssignableFrom(sourceField.getFieldType())||
                    long.class.isAssignableFrom(sourceField.getFieldType())
                    ){
                return true;
            }
           
        }
        if(char.class.isAssignableFrom(targetField.getFieldType())||
                boolean.class.isAssignableFrom(targetField.getFieldType())||
                char[].class.isAssignableFrom(targetField.getFieldType())||
                double.class.isAssignableFrom(targetField.getFieldType())||
                float.class.isAssignableFrom(targetField.getFieldType())||
                int.class.isAssignableFrom(targetField.getFieldType())||
                long.class.isAssignableFrom(targetField.getFieldType())){

        }
        return false;
    }
 
    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName,MappingField sourceField, MappingField targetField) {
        return Common.valueAssign(sourceField, targetField);
    }
    
}
