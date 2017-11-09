package org.smapgen.sdm.map.mappers.itemcontiner;

import org.smapgen.dconf.Dconf;
import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.common.ConstantValues;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.itemcontiner.common.ItemContinerMap;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ArrayMap implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        return MappingType.ARRAY.equals(sourceField.getGetterGenericType()) && MappingType.ARRAY.equals(targetField
                .getSetterGenericType());
    }

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, String targetName, MappingField sourceField,
            MappingField targetField) throws Throwable {

        StringBuffer buffer = new StringBuffer();
        String newSourceName = Common.createVar(buffer, sourceField, sourceField.getFieldType());
        if (sourceField.getFieldType().equals(targetField.getFieldType())) {
            buffer.append(Common.preBlock(newSourceName));
            buffer.append(Common.valueAssign(newSourceName, targetField));
            buffer.append(Common.postBlock());
            return buffer;
        }
        final String newtargetName = Common.createNewVarArray(buffer, targetField.getFieldType());
        buffer.append(mapperArrays(sourceField, targetField, newSourceName, newtargetName));

        return buffer;
    }

    /**
     * @param sourceField
     * @param targetField
     * @param sourceName
     * @param targetName
     * @return
     * @throws Throwable
     */
    private StringBuffer mapperArrays(final MappingField sourceField, final MappingField targetField,
            final String sourceName, final String targetName) throws Throwable {
        StringBuffer b = new StringBuffer();

        // If source is an array, classTarget must be an array too
        pre(sourceField, targetField, sourceName, targetName, b);
        b.append(ItemContinerMap.mapItemElement(sourceField, targetField,ConstantValues.ClassMapper_elementPrefix + sourceName, targetName));
        post(targetField, targetName, b);

        return b;
    }

    /**
     * @param targetField
     * @param targetName
     * @param b
     */
    private void post(final MappingField targetField, final String targetName, StringBuffer b) {
        b.append('}')
        .append(targetName).append(" = list").append(targetName).append(".toArray(new ")
        .append(targetField.getFieldType().getCanonicalName()).append("[0]);")
        .append(Common.valueAssign(targetName, targetField))
        .append('}').append(System.getProperty("line.separator"));
    }

    /**
     * @param sourceField
     * @param targetField
     * @param sourceName
     * @param targetName
     * @param b
     * @throws Throwable 
     */
    private void pre(final MappingField sourceField, final MappingField targetField, final String sourceName,
            final String targetName, StringBuffer b) throws Throwable {
        b.append("if(");
        if(!Dconf.getInstance().containsNotNullAnot(targetField.getAnotations())){
            b.append(Common.nullValidation(sourceName)).append(" && ");
        }
        b.append(sourceName).append(".length>0 ){ java.util.List<")
                .append(targetField.getFieldType().getCanonicalName()).append("> list")
                .append(targetName).append(" = new java.util.ArrayList<")
                .append(targetField.getFieldType().getCanonicalName()).append(">(); for(")
                .append(sourceField.getFieldType().getCanonicalName()).append(" el").append(sourceName)
                .append(" : ").append(sourceName).append("){");
    }
}
