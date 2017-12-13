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
    
    private Integer compatThreshold;

    /**
     * @return the compatThreshold
     */
    public Integer getCompatThreshold() {
        return compatThreshold;
    }

    /**
     * @param compatThreshold the compatThreshold to set
     */
    public void setCompatThreshold(Integer compatThreshold) {
        this.compatThreshold = compatThreshold;
    }

    public SDataObjMapperConfig( String input, String output, Integer compatThreshold) {
        this.INSufix = input;
        this.OUTSufix = output;
        if(compatThreshold!=null){
            this.compatThreshold = compatThreshold;
        }
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