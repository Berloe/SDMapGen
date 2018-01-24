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

    public SDataObjMapperConfig(final String input, final String output, final Integer compatThreshold) {
        INSufix = input;
        OUTSufix = output;
        if (compatThreshold != null) {
            this.compatThreshold = compatThreshold;
        }
    }

    /**
     * @return the compatThreshold
     */
    public Integer getCompatThreshold() {
        return compatThreshold;
    }

    public String getINSufix() {
        return INSufix;
    }

    public String getOUTSufix() {
        return OUTSufix;
    }

    /**
     * @param compatThreshold
     *            the compatThreshold to set
     */
    public void setCompatThreshold(final Integer compatThreshold) {
        this.compatThreshold = compatThreshold;
    }

    public void setINSufix(final String iNSufix) {
        INSufix = iNSufix;
    }

    public void setOUTSufix(final String oUTSufix) {
        OUTSufix = oUTSufix;
    }
}