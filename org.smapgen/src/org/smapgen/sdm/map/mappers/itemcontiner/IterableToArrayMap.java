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
public class IterableToArrayMap implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        return MappingType.ARRAY.equals(sourceField.getGetterGenericType())
                && MappingType.COLLECTION.equals(targetField.getSetterGenericType());
    }

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map( final String sourceName,String targetName,
            MappingField sourceField, MappingField targetField) throws Throwable {
        
        if (sourceField.getCalculatedFieldType().getCanonicalName().equals(Object.class.getCanonicalName())) {
            return new StringBuffer();
        }
        if (targetField.getFieldType().getComponentType().equals(sourceField.getCalculatedFieldType())) {
            return Common.valueAssign(sourceName, targetField);
        }

        Object datoTarget;
        // Mapping Collection
        final StringBuffer auxSb = new StringBuffer();
        datoTarget = ObjectFactory.loader(targetField.getFieldType()/*.getResolvedAbsClss()*/);
        final String newtargetName = Common.createNewVarArray(auxSb, datoTarget);
        auxSb.append(mapperArrayfromCollection(sourceField, targetField, sourceName, newtargetName));
            
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
    private StringBuffer mapperArrayfromCollection(final MappingField sourceField, final MappingField targetField,
             final String sourceName, final String targetName) throws Throwable {
        final StringBuffer b = (new StringBuffer()).append("if(!").append(sourceName).append(".isEmpty()){ java.util.List<").append(targetField.getCalculatedFieldType().getCanonicalName())
                .append("> list").append(targetName).append(" = new java.util.ArrayList<")
                .append(targetField.getCalculatedFieldType().getCanonicalName()).append(">(); for(")
                .append(sourceField.getFieldType().getComponentType().getCanonicalName()).append(" el")
                .append(sourceName).append(" : ").append(sourceName).append("){");

        ItemContinerMap itemContinerMap = new ItemContinerMap();
        
        b.append(itemContinerMap.mapItemElement(sourceField, targetField, ConstantValues.ClassMapper_elementPrefix + sourceName,targetName))
            .append("}").append(targetName).append(" = list").append(targetName).append(".toArray(new ")
            .append(targetField.getFieldType().getComponentType().getCanonicalName()).append("[0]);")
            .append(Common.valueAssign(targetName, targetField))
            .append("}");
        return b;
    }
}
