package com.linkedlogics.diameter.object;

/**
 * Created by shnovruzov on 16/5/2018.
 */
public enum VendorType {

    None(0, "None"),
    HP(11, "HP"),
    Sun(42, "Sun"),
    Merit(61, "Merit"),
    Ericsson(193, "Ericsson"),
    USR(429, "USR"),
    GPP2(5535, "3GPP2"),
    TGPP(10415, "TGPP"),
    Vodafone(12645, "Vodafone"),
    ETSI(13019, "ETSI"),
    CableLabs(4491, "CableLabs"),
    Azerfon(40004, "org.azerfon");

    private long code;
    private String name;

    private VendorType(long code, String name) {
        this.code = code;
        this.name = name;
    }

    public long getCode() {
        return code;
    }

    public static VendorType getByName(String name) {
        for (VendorType type : VendorType.values())
            if (type.name.equals(name)) return type;
        return null;
    }

}
