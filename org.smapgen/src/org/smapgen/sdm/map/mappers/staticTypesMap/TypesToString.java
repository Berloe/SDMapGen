package org.smapgen.sdm.map.mappers.statictypesmap;

import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.metadata.MappingField;

public class TypesToString implements IMapper {

    @Override
    public StringBuffer map(String sourceName, String targetName, MappingField sourceField, MappingField targetField)
            throws Throwable {
        StringBuffer sb = new StringBuffer();
        sb.append(targetName + "." + targetField.getSetterMethod().getName() + "((new String()).valueOf("
                + intValueOf(sourceField, sourceName) + "));");
        return sb;
    }

    private String intValueOf(MappingField sourceField, String sourceName) {
        if (sourceField.getFieldType().getCanonicalName().equals(int.class.getCanonicalName())) {
            return sourceName + "." + sourceField.getGetterMethod().getName() + "()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(Integer.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "().intValue()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(Long.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "().longValue()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(long.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(Double.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "().doubleValue()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(double.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(Float.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "().floatValue()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(float.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(Character.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "().charValue()";
        }else if(sourceField.getFieldType().getCanonicalName().equals(char.class.getCanonicalName())){
            return sourceName + "." + sourceField.getGetterMethod().getName() + "()";
        }
        return null;
    }

    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        return (targetField.getFieldType().getCanonicalName().equals(String.class.getCanonicalName()) &&
                (sourceField.getFieldType().getCanonicalName().equals(Integer.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(int.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(Long.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(long.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(Double.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(double.class.getCanonicalName()))
                || sourceField.getFieldType().getCanonicalName().equals(Float.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(float.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(char.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(Character.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(byte.class.getCanonicalName())
                || sourceField.getFieldType().getCanonicalName().equals(Byte.class.getCanonicalName()));
    }

}
