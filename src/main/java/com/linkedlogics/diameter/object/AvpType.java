package com.linkedlogics.diameter.object;

/**
 * Created by shnovruzov on 16/5/2018.
 */
public enum AvpType {

    OctetString,
    Integer32,
    Integer64,
    Unsigned32,
    Unsigned64,
    Float32,
    Float64,
    Grouped,
    Address,
    Time,
    UTF8String,
    DiameterIdentity,
    DiameterURI,
    Enumerated,
    IPFilterRule,
    QoSFilterRule,
    QOSFilterRule,
    Unsigned32Enumerated,
    MIPRegistrationRequest,
    AppId,
    VendorId,
    IPAddress;

    public static AvpType getByName(String name) {
        return AvpType.valueOf(name);
    }

}
