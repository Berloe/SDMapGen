package org.smapgen.sdm;

import org.smapgen.sdm.common.Common;
import org.smapgen.sdm.common.ConstantValues;
import org.smapgen.sdm.config.SDataObjMapperConfig;
import org.smapgen.sdm.factory.ObjectFactory;
import org.smapgen.sdm.map.MapperClassElement;
import org.smapgen.sdm.registry.Registry;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class SimpleDataObjMapper implements ISimpleDataObjMapper {

    /**
     * @param conf
     */
    public SimpleDataObjMapper(final SDataObjMapperConfig conf) {
        config.setINSufix(conf.getINSufix());
        config.setOUTSufix(conf.getOUTSufix());
        config.setCompatThreshold(conf.getCompatThreshold());
    }
    /**
     * @param clasSource
     * @param classTarget
     * @param isMainPrivate
     * @return
     * @throws Throwable
     */
    public StringBuffer[] mappers(final Class<?> clasSource, final Class<?> classTarget, Boolean isMainPrivate)
            throws Throwable {
        final Object target = ObjectFactory.loader(classTarget);
        final Object source = ObjectFactory.loader(clasSource);
        final StringBuffer b = new StringBuffer();
        final String sourceName = Common.genName(source, Boolean.TRUE);
        final String targetName = Common.createNewVar(b, target);
        b.append(MapperClassElement.mapperInstance( source, target, sourceName, targetName));
        Common.addMappingMethod(b, clasSource, classTarget, targetName, sourceName,
                ConstantValues.ClassMapper_mapPrefix + classTarget.getSimpleName(), isMainPrivate);
        StringBuffer[] flist = Registry.getInstance().getFunctionList().toArray(new StringBuffer[0]);
        Registry.dispose();
        return flist;
    }
    
    public void preLoadFunction(String source, String target, String fName) {
        Registry.registreFunction(source, target, fName);
    }
    
}
