package org.smapgen.sdm.map.mappers;

import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ToIntegerMap extends Mapper implements IMapper {

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(final MappingField sourceField, final MappingField targetField) {
        if ((targetField.getFieldType().equals(Integer.class) || targetField.getFieldType().equals(int.class)) && (isPrimitive(sourceField) || isAssignablePrimitive(sourceField))) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName, final MappingField sourceField, final MappingField targetField) {
        if (sourceField.getFieldType().equals(byte[].class) || sourceField.getFieldType().equals(char[].class)) {
            return new StringBuffer(targetField.getVarName() + "." + targetField.getSetterMethod().getName() + "(new Integer(new String(" + sourceField.getVarName() + ")));");
        } else if (sourceField.getFieldType().equals(int.class)) {
            return new StringBuffer(targetField.getVarName() + "." + targetField.getSetterMethod().getName() + "(" + sourceField.getVarName() + ");");
        } else if (sourceField.getFieldType().equals(int.class) && targetField.getFieldType().equals(Integer.class)) {
            return new StringBuffer(targetField.getVarName() + "." + targetField.getSetterMethod().getName() + "(new Integer(" + sourceField.getVarName() + ").intValue());");
        } else if (sourceField.getFieldType().equals(byte.class)) {
            return new StringBuffer(targetField.getVarName() + "." + targetField.getSetterMethod().getName() + "(new Byte(" + sourceField.getVarName() + ").intValue();");
        } else if (sourceField.getFieldType().equals(Byte.class)) {
            return new StringBuffer(targetField.getVarName() + "." + targetField.getSetterMethod().getName() + "(" + sourceField.getVarName() + ").intValue();");
        }
        return new StringBuffer();
    }

    /**
     * @param sourceField
     */
    private Boolean isAssignablePrimitive(final MappingField sourceField) {
        return char[].class.isAssignableFrom(sourceField.getFieldType()) || byte[].class.isAssignableFrom(sourceField.getFieldType()) || int.class.isAssignableFrom(sourceField.getFieldType()) || byte.class.isAssignableFrom(sourceField.getFieldType());
    }

    /**
     * @param sourceField
     * @return
     */
    private boolean isPrimitive(final MappingField sourceField) {
        return sourceField.getFieldType().equals(char[].class) || sourceField.getFieldType().equals(byte[].class) || sourceField.getFieldType().equals(int.class) || sourceField.getFieldType().equals(byte.class);
    }
}