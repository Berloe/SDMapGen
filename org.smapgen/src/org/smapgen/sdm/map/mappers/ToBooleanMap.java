package org.smapgen.sdm.map.mappers;

import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ToBooleanMap extends Mapper implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        if((targetField.getFieldType().equals(Boolean.class)||targetField.getFieldType().equals(boolean.class))&&(!targetField.getFieldType().equals(sourceField.getFieldType()))){
            if (sourceField.getFieldType().equals(boolean.class)||sourceField.getFieldType().equals(Boolean.class)||sourceField.getFieldType().equals(String.class)){
                return true;
            }
        }
        return false;
    }

 
    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName,MappingField sourceField, MappingField targetField) {
        if(targetField.getFieldType().equals(boolean.class)){
        	if(sourceField.getFieldType().equals(Boolean.class)) {
        		return (new StringBuffer()).append(targetField.getVarName() + " = " +sourceField.getVarName()+".booleanValue();");
        	}else if(sourceField.getFieldType().equals(String.class)){
           		return (new StringBuffer()).append(targetField.getVarName() + " = Boolean.parseBoolean(" + sourceField.getVarName()+").booleanValue();");
        	}
        }else if(targetField.getFieldType().equals(Boolean.class)){
        	if(sourceField.getFieldType().equals(boolean.class)) {
        		return (new StringBuffer()).append(targetField.getVarName() + " = new Boolean(" +sourceField.getVarName()+");");
        	}else if(sourceField.getFieldType().equals(String.class)){
           		return (new StringBuffer()).append(targetField.getVarName() + " = Boolean.parseBoolean(" + sourceField.getVarName()+");");
        	}
        }
		return new StringBuffer();
    }
}