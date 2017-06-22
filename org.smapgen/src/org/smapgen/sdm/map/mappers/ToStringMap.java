package org.smapgen.sdm.map.mappers;

import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ToStringMap extends Mapper implements IMapper {

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
            } else{
                return isAssignablePrimitive(sourceField);
            }
        }

        return false;
    }

    /**
     * @param sourceField
     */
    private Boolean isAssignablePrimitive(MappingField sourceField) {
        if(char.class.isAssignableFrom(sourceField.getFieldType())||
                boolean.class.isAssignableFrom(sourceField.getFieldType())||
                char[].class.isAssignableFrom(sourceField.getFieldType())||
                double.class.isAssignableFrom(sourceField.getFieldType())||
                float.class.isAssignableFrom(sourceField.getFieldType())||
                int.class.isAssignableFrom(sourceField.getFieldType())||
                long.class.isAssignableFrom(sourceField.getFieldType())
                ){
            return true;
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
        }else if(isAssignablePrimitive(sourceField)){
            return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                    + "("+sourceField.getVarName()+".toString());");
        }else{
            return (new StringBuffer()).append(targetField.getVarName() + "." + targetField.getSetterMethod().getName()
                + "(new String().valueOf(" +sourceField.getVarName()+"));");
        }

    }
    
}
