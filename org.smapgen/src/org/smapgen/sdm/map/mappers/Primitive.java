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
        if(targetField.getFieldType().equals(Boolean.class) && (sourceField.getFieldType().equals(boolean.class)|| sourceField.getFieldType().equals(String.class))){
                return true;
        }else if(targetField.getFieldType().equals(Integer.class) && (sourceField.getFieldType().equals(int.class)|| sourceField.getFieldType().equals(String.class))){
            return true;(new Integer()).
        }else if(targetField.getFieldType().equals(Double.class) && (sourceField.getFieldType().equals(double.class)|| sourceField.getFieldType().equals(String.class))){
            return true;  (new Double())
        }else if(targetField.getFieldType().equals(Long.class) && (sourceField.getFieldType().equals(long.class)|| sourceField.getFieldType().equals(String.class))){
            return true; (new Long()).
            
        }
        return false;
    }
 
    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName,MappingField sourceField, MappingField targetField) {
        if(sourceField.getFieldType().equals(byte[].class)){
            return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                    + "(new String("+sourceField.getVarName()+"));");
        }
        return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                + "(new String().valueOf(" +sourceField.getVarName()+"));");
    }
    
}
