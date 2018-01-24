package org.smapgen.sdm.map.mappers.instantiable;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.utils.Utils;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class ExtendedMap extends InstanceOfMap implements IMapper {

    /*
     * (non-Javadoc)
     * @see org.sdm.map.IMapper#isAplicable(org.sdm.metadata.MappingField, org.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(final MappingField sourceField, final MappingField targetField) {

        try {
            return !Utils.isAbstract(sourceField.getFieldType()) && Utils.getConcreteClasses(sourceField.getFieldType()).length > 1;
        } catch (final Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName, final MappingField sourceField, final MappingField targetField) throws Throwable {
        final String sourceCalculatedType = sourceField.getCalculatedFieldType().isArray() ? sourceField.getField().getType().getComponentType().getSimpleName() : sourceField.getCalculatedFieldType().getSimpleName();
        final Class<?>[] excluded = sourceCalculatedType.equals(sourceField.getFieldType().getSimpleName()) ? Utils.getConcreteClasses(sourceField.getFieldType()) : new Class<?>[] {};
        final Object datoSource = ObjectFactory.loader(sourceField.getFieldType()/* .getResolvedAbsClss() */);

        final String newSourceName = excluded.length > 0 ? sourceName : Common.genName(datoSource, Boolean.FALSE);

        return instanceOfMapping(sourceField, targetField, sourceName, newSourceName, targetName, excluded);

    }
}
