package org.smapgen.sdm.map.mappers;

import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ToLongMap extends Mapper implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        if(((targetField.getFieldType().equals(Integer.class) || (targetField.getFieldType().equals(int.class)))
                && (isPrimitive(sourceField) || isAssignablePrimitive(sourceField)))){
            return true;
        }
        return false;
    }

    /**
     * @param sourceField
     * @return
     */
    private boolean isPrimitive(MappingField sourceField) {
        return sourceField.getFieldType().equals(char[].class)||
            sourceField.getFieldType().equals(byte[].class)||
            sourceField.getFieldType().equals(int.class)||
            sourceField.getFieldType().equals(long.class)||
            sourceField.getFieldType().equals(byte.class);
    }

    /**
     * @param sourceField
     */
    private Boolean isAssignablePrimitive(MappingField sourceField) {
        return  char[].class.isAssignableFrom(sourceField.getFieldType())||
                byte[].class.isAssignableFrom(sourceField.getFieldType())||
                int.class.isAssignableFrom(sourceField.getFieldType())||
                long.class.isAssignableFrom(sourceField.getFieldType())||
                byte.class.isAssignableFrom(sourceField.getFieldType());
    }
 
    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName,MappingField sourceField, MappingField targetField) {
        if(sourceField.getFieldType().equals(byte[].class)||sourceField.getFieldType().equals(char[].class)){
            return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                    + "(new Long(new String("+sourceField.getVarName()+")));");
        }else if(sourceField.getFieldType().equals(int.class)){
            return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                    + "("+sourceField.getVarName()+");");
        }else if(sourceField.getFieldType().equals(int.class) && targetField.getFieldType().equals(Long.class)){
            return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                    + "(new Long("+sourceField.getVarName()+"));");
        } else if(sourceField.getFieldType().equals(byte.class)){
            return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                + "(new Byte(" +sourceField.getVarName()+").longValue();");
        }else if(sourceField.getFieldType().equals(Byte.class)){
            return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                    + "(" +sourceField.getVarName()+").longValue();");
            }
        return new StringBuffer();
    }
}