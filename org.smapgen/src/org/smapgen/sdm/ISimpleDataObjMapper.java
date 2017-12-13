package org.smapgen.sdm;

import org.smapgen.sdm.config.SDataObjMapperConfig;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public interface ISimpleDataObjMapper {
    /**
     * Default sufix configuration
     */
    SDataObjMapperConfig config = new SDataObjMapperConfig("_IN","_Out", new Integer(0));

}