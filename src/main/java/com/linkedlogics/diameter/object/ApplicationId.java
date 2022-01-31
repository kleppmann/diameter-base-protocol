package com.linkedlogics.diameter.object;

import java.io.Serializable;

/**
 * Created by shnovruzov on 16/6/2018.
 */
public final class ApplicationId implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final long UNDEFINED_VALUE = 0L;
    private long venId = 0L;
    private long authId = 0L;
    private long acctId = 0L;

    public static ApplicationId createByAuthAppId(long authAppId) {
        return new ApplicationId(0L, authAppId, 0L);
    }

    public static ApplicationId createByAccAppId(long acchAppId) {
        return new ApplicationId(0L, 0L, acchAppId);
    }

    public static ApplicationId createByAuthAppId(long vendorId, long authAppId) {
        return new ApplicationId(vendorId, authAppId, 0L);
    }

    public static ApplicationId createByAccAppId(long vendorId, long acchAppId) {
        return new ApplicationId(vendorId, 0L, acchAppId);
    }

    private ApplicationId(long vendorId, long authAppId, long acctAppId) {
        this.authId = authAppId;
        this.acctId = acctAppId;
        this.venId = vendorId;
    }

    public long getVendorId() {
        return this.venId;
    }

    public long getAuthAppId() {
        return this.authId;
    }

    public long getAcctAppId() {
        return this.acctId;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof ApplicationId)) {
            return false;
        } else {
            ApplicationId appId = (ApplicationId)obj;
            return this.authId == appId.authId && this.acctId == appId.acctId && this.venId == appId.venId;
        }
    }

    public int hashCode() {
        int result = (int)(this.venId ^ this.venId >>> 32);
        result = 31 * result + (int)(this.authId ^ this.authId >>> 32);
        result = 31 * result + (int)(this.acctId ^ this.acctId >>> 32);
        return result;
    }

    public String toString() {
        return "AppId [" + "Vendor-Id:" + this.venId + "; Auth-Application-Id:" + this.authId + "; Acct-Application-Id:" + this.acctId + "]";
    }

    public interface Ranges {
        long STANDARDS_TRACK_APPLICATIONS_MIN = 1L;
        long STANDARDS_TRACK_APPLICATIONS_MAX = 16777215L;
        long VENDOR_SPECIFIC_APPLICATIONS_MIN = 16777216L;
        long VENDOR_SPECIFIC_APPLICATIONS_MAX = -2L;
    }

    public interface Standard {
        long DIAMETER_COMMON_MESSAGE = 0L;
        long NASREQ = 1L;
        long MOBILE_IP = 2L;
        long DIAMETER_BASE_ACCOUNTING = 3L;
        long RELAY = -1L;
    }
}
