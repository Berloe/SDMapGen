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
public class ArrayToIterableMap implements IMapper {

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(MappingField sourceField, MappingField targetField) {
        return MappingType.COLLECTION.equals(sourceField.getGetterGenericType())
                && MappingType.ARRAY.equals(targetField.getSetterGenericType());
    }

    /* (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName,String targetName,
            MappingField sourceField, MappingField targetField) throws Throwable {

        if (targetField.getCalculatedFieldType().getCanonicalName().equals(Object.class.getCanonicalName())
                || sourceField.getCalculatedFieldType().getCanonicalName().equals(Object.class.getCanonicalName())) {
            return new StringBuffer();
        }
        if (targetField.getCalculatedFieldType().equals(sourceField.getCalculatedFieldType())) {
            return Common.valueAssign(sourceName, targetField);
        }

        final StringBuffer auxSb = new StringBuffer();
        Object datoTarget = ObjectFactory.loader(targetField.getCalculatedFieldType());
        final String newtargetName = Common.createNewVarCollection(auxSb, datoTarget, targetField);
        auxSb.append(mapperCollectionsfromArray(sourceField, targetField, sourceName, newtargetName));
            auxSb.append(Common.valueAssign(newtargetName, targetField));

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
    private StringBuffer mapperCollectionsfromArray(final MappingField sourceField, final MappingField targetField, final String sourceName, final String targetName) throws Throwable {
        final StringBuffer b = new StringBuffer();
        // If source is an array, classTarget must be an array too
        pre(sourceField, sourceName, b);
        b.append((new ItemContinerMap()).mapItemElement(sourceField, targetField, ConstantValues.ClassMapper_elementPrefix + sourceName, 
                targetName));
        post(b);
        return b;
    }

    /**
     * @param b
     */
    private void post(final StringBuffer b) {
        b.append("}").append("}");
    }

    /**
     * @param sourceField
     * @param sourceName
     * @param b
     */
    private void pre(final MappingField sourceField, final String sourceName, final StringBuffer b) {
        b.append("if(").append(sourceName).append(".length>0 ){");
        b.append("for(").append(sourceField.getCalculatedFieldType().getCanonicalName()).append(" el")
                .append(sourceName).append(" : ").append(sourceName).append("){");
    }
}
