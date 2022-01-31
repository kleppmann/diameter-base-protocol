package com.linkedlogics.diameter.object;

import com.linkedlogics.diameter.exception.ParseException;
import com.linkedlogics.diameter.parser.AvpTypeParser;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class AvpList implements Serializable, Iterable<Avp> {

    private List<Avp> avps;

    public AvpList() {
        avps = new ArrayList<Avp>();
    }

    public Avp getAvp(int code) {
        for (Avp avp : avps)
            if (avp.getCode() == code) return avp;
        return null;
    }

    public AvpList getAvps(int avpCode) {
        AvpList result = new AvpList();
        for (Avp avp : avps) {
            if (avp.getCode() == avpCode) {
                result.addAvp(avp);
            }
        }
        return result;
    }

    public Avp getAvpByIndex(int index) {
        if (index < 0 || index >= avps.size()) return null;
        return avps.get(index);
    }

    public boolean removeAvp(int code) {
        Iterator<Avp> iterator = iterator();
        while (iterator.hasNext()) {
            Avp avp = iterator.next();
            if (avp.getCode() == code) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean removeAvp(int code, long vendorId) {
        Iterator<Avp> iterator = iterator();
        while (iterator.hasNext()) {
            Avp avp = iterator.next();
            if (avp.getCode() == code && avp.getVendorId() == vendorId) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public void addAvp(Avp avp) {
        if (avp != null) avps.add(avp);
    }

    public void addAvp(int code, long value, boolean isUnsigned) {
        Avp avp = new Avp(code, 0, 0L, isUnsigned ? AvpTypeParser.unsignedInt32ToBytes(value) : AvpTypeParser.int64ToBytes(value));
        avps.add(avp);
    }

    public void addAvp(int code, long value, boolean mFlag, boolean pFlag, boolean isUnsigned) {
        int flags = (mFlag ? 64 : 0) | (pFlag ? 32 : 0);
        Avp avp = new Avp(code, flags, 0L, isUnsigned ? AvpTypeParser.unsignedInt32ToBytes(value) : AvpTypeParser.int64ToBytes(value));
        avps.add(avp);
    }

    public Avp addAvp(int code, String value, boolean mFlag, boolean pFlag, boolean asOctetString) {
        int flags = ((mFlag ? 0x40 : 0) | (pFlag ? 0x20 : 0));
        try {
            Avp res = new Avp(code, flags, 0, asOctetString ? AvpTypeParser.octetStringToBytes(value) : AvpTypeParser.utf8StringToBytes(value));
            avps.add(res);
            return res;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, String value, boolean asOctetString) {
        try {
            Avp avp = new Avp(avpCode, 0, 0L, asOctetString ? AvpTypeParser.octetStringToBytes(value) : AvpTypeParser.utf8StringToBytes(value));
            this.avps.add(avp);
            return avp;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, InetAddress value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40 : 0) | (pFlag ? 0x20 : 0));
        Avp res = new Avp(avpCode, flags, 0, AvpTypeParser.addressToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, int value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40 : 0) | (pFlag ? 0x20 : 0));
        Avp res = new Avp(avpCode, flags, 0, AvpTypeParser.int32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public AvpList addGroupedAvp(int avpCode, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40 : 0) | (pFlag ? 0x20 : 0));
        Avp avp = new Avp(avpCode, flags, 0, new byte[0]);
        avp.setAvpGrouped(new AvpList());
        this.avps.add(avp);
        return avp.getAvpList();
    }

    public Iterator<Avp> iterator() {
        return avps.iterator();
    }

    public int size() {
        return avps.size();
    }

}
