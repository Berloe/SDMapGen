package org.smapgen.sdm.map.mappers.itemcontiner;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.common.ConstantValues;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.itemcontiner.common.ItemContinerMap;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class IterableMap implements IMapper {

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        return MappingType.COLLECTION.equals(sourceField.getGetterGenericType())
                && MappingType.COLLECTION.equals(targetField.getSetterGenericType());
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, String targetName, MappingField sourceField,
            MappingField targetField) throws Throwable {
        if (targetField.getCalculatedFieldType().getCanonicalName().equals(Object.class.getCanonicalName())) {
            return new StringBuffer();
        }
        if (sourceField.getFieldType().getComponentType().equals(targetField.getCalculatedFieldType())) {

            return Common.valueAssign(sourceName, targetField);
        }

        final StringBuffer auxSb = new StringBuffer();
        Object datoTarget = ObjectFactory.loader(targetField.getCalculatedFieldType());
        final String newtargetName = Common.createNewVarCollection(auxSb, datoTarget, targetField);
        auxSb.append(mapperCollections(sourceField, targetField, sourceName, newtargetName));

        return auxSb;
    }

    /**
     * @param sourceField
     * @param targetField
     * @param sourceName
     * @param targetName
     * @return
     * @throws Throwable
     */
    private StringBuffer mapperCollections(final MappingField sourceField, final MappingField targetField,
            final String sourceName, final String targetName) throws Throwable {
        // If source is an array, classTarget must be an array too
        final StringBuffer b = (new StringBuffer()).append("if(!").append(sourceName).append(".isEmpty()){ for(")
                .append(sourceField.getCalculatedFieldType().getCanonicalName()).append(" el")
                .append(sourceName).append(" : ").append(sourceName).append("){");
        
        sourceField.setFieldType(sourceField.getCalculatedFieldType());
        targetField.setFieldType(targetField.getCalculatedFieldType());
        ItemContinerMap itemContinerMap = new ItemContinerMap();
        b.append(itemContinerMap.mapItemElement(sourceField, targetField,ConstantValues.ClassMapper_elementPrefix + sourceName, targetName))
        .append("}").append(Common.valueAssign(targetName, targetField)).append("}");
        
        return b;
    }
}
