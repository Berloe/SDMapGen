package org.smapgen.sdm.map.mappers;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.map.mappers.common.Mapper;
import org.smapgen.sdm.metadata.MappingField;
import org.smapgen.sdm.metadata.MappingType;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class DateMap extends Mapper implements IMapper {

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#isAplicable(org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public Boolean isAplicable(final MappingField sourceField, final MappingField targetField) {
        return MappingType.DATE.equals(targetField.getSetterGenericType()) && (MappingType.DATE.equals(sourceField.getGetterGenericType()) || MappingType.CALENDAR.equals(sourceField.getGetterGenericType()));
    }

    /*
     * (non-Javadoc)
     * @see org.smapgen.sdm.map.IMapper#map(java.lang.String, java.lang.String, org.smapgen.sdm.metadata.MappingField, org.smapgen.sdm.metadata.MappingField)
     */
    @Override
    public StringBuffer map(final String sourceName, final String targetName, final MappingField sourceField, final MappingField targetField) {
        // Mapping Date
        final StringBuffer buffer = new StringBuffer();
        if (MappingType.DATE.equals(sourceField.getGetterGenericType())) {
            buffer.append(Common.valueAssign(sourceField, targetField));
        } else {
            final Calendar date = new GregorianCalendar();
            final String calendarName = Common.createNewVar(buffer, date);
            buffer.append(calendarName).append(".setTime(").append(sourceName).append(".getTimeInMillis());");
            buffer.append(Common.valueAssignTimeInMilllis(targetField, calendarName));
        }
        return buffer;
    }

}
