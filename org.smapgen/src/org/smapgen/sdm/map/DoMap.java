package org.smapgen.sdm.map;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smapgen.dconf.Dconf;
import org.smapgen.scl.exception.ClassLoaderException;
import org.smapgen.sdm.map.mappers.common.IMapper;
import org.smapgen.sdm.metadata.MappingField;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class DoMap {
    /**
     * @param sourceName
     * @param targetName
     * @param sourceField
     * @param targetField
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws Throwable
     * @throws ClassLoaderException
     */
    public static StringBuffer mapSourceIntoTarget( final String sourceName, final String targetName, final MappingField sourceField, final MappingField targetField)
            throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException, Throwable, ClassLoaderException {
        List<IMapper> mops = Dconf.getInstance().getMapper();
        StringBuffer b = new StringBuffer();
        for (IMapper mapperOps : mops) {
            if (mapperOps.isAplicable(sourceField, targetField)){
                b.append(mapperOps.map(sourceName, targetName, sourceField, targetField));
                return b;
            }
        }
        return b;
    }

}
