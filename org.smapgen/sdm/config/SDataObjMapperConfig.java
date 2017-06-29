package org.smapgen.sdm.config;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class SDataObjMapperConfig {
    /**
     * String INSufix.
     */
    private String INSufix;
  
    /**
     * String OUTSufix.
     */
    private String OUTSufix;

    public SDataObjMapperConfig( String input, String output) {
        this.INSufix = input;
        this.OUTSufix = output;
    }

    public String getINSufix() {
        return INSufix;
    }

    public void setINSufix(String iNSufix) {
        INSufix = iNSufix;
    }

    public String getOUTSufix() {
        return OUTSufix;
    }

    public void setOUTSufix(String oUTSufix) {
        OUTSufix = oUTSufix;
    }
}