package com.linkedlogics.diameter.object;

import com.linkedlogics.application.logger.Logger;
import com.linkedlogics.application.logger.LoggerLevel;
import com.linkedlogics.diameter.exception.AvpException;
import com.linkedlogics.diameter.parser.AvpParser;
import com.linkedlogics.diameter.parser.AvpTypeParser;
import com.linkedlogics.diameter.data.AvpManager;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class Avp implements Serializable {

    private int code;
    private String name;
    private long vendorId;
    private int flags;
    private int length;
    private boolean isVendorId = false;
    private boolean isMandatory = true;
    private boolean isProtected = false;
    private byte[] rawData = new byte[0];
    private AvpList avpGrouped;
    private AvpType avpType;

    public Avp(int code, String name, AvpType dataType) {
        this.code = code;
        this.name = name;
        this.avpType = dataType;
    }

    public Avp(int code, int flags, long vendorId, byte[] rawData) {
        this.code = code;
        this.avpType = AvpManager.getInstance().getAvp(code, vendorId).getAvpType();
        this.name = AvpManager.getInstance().getAvp(code, vendorId).getName();
        this.flags = flags;
        this.vendorId = vendorId;
        this.isVendorId = (flags & 0x80) != 0;
        this.isMandatory = (flags & 0x40) != 0;
        this.isProtected = (flags & 0x20) != 0;
        this.rawData = rawData;
        length = 8 + (isVendorId ? 4 : 0) + rawData.length;
    }

    public Avp(Avp avp) {
        this.code = avp.getCode();
        this.vendorId = avp.getVendorId();
        this.isMandatory = avp.isMandatory();
        this.isProtected = avp.isProtected();
        this.isVendorId = avp.isVendorId();
        try {
            rawData = avp.getRawData();
            if (rawData == null || rawData.length == 0) {
                avpGrouped = avp.getAvpGrouped();
            }
        } catch (AvpException e) {
            Logger.log(LoggerLevel.DEBUG, e, "Can not create Avp");
        }
    }

    public Avp(int code, Avp avp) {
        this(avp);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public long getVendorId() {
        return vendorId;
    }

    public int getFlags() {
        return flags;
    }

    public AvpType getAvpType() {
        return avpType;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public boolean isVendorId() {
        return isVendorId;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public AvpList getAvpList(){
        return avpGrouped;
    }

    public Object getValue() {
        try {
            switch (avpType) {
                case OctetString:
                    return getUTF8String();
                case Unsigned32:
                    return getUnsigned32();
                case Unsigned64:
                    return getUnsigned64();
                case Integer32:
                    return getInteger32();
                case Integer64:
                    return getInteger64();
                case Float32:
                    return getFloat32();
                case Float64:
                    return getFloat64();
                case Grouped:
                    return rawData.length;
                case Address:
                    return getAddress();
                case Time:
                    return getTime();
                case UTF8String:
                    return getUTF8String();
                case DiameterIdentity:
                    return getDiameterIdentity();
                case DiameterURI:
                    return getDiameterURI();
                case Enumerated:
                    return getEnumerated();
                default:
                    return "Unknown type";
            }

        } catch (Exception e) {

        }

        return null;
    }

    public byte[] getOctetString() throws AvpException {
        return rawData;
    }

    public String getUTF8String() throws UnsupportedEncodingException {
        return AvpTypeParser.bytesToUtf8String(rawData);
    }

    public int getInteger32() throws AvpException {
        return AvpTypeParser.bytesToInt32(rawData);
    }

    public long getInteger64() throws AvpException {
        return AvpTypeParser.bytesToInt64(rawData);
    }

    public long getUnsigned32() throws AvpException {
        byte[] bytes = new byte[8];
        System.arraycopy(rawData, 0, bytes, 4, 4);
        return AvpTypeParser.bytesToInt64(bytes);
    }

    public long getUnsigned64() throws AvpException {
        return AvpTypeParser.bytesToInt64(rawData);
    }

    public float getFloat32() throws AvpException {
        return AvpTypeParser.bytesToFloat32(rawData);
    }

    public double getFloat64() throws AvpException {
        return AvpTypeParser.bytesToFloat64(rawData);
    }

    public InetAddress getAddress() throws AvpException {
        return AvpTypeParser.bytesToAddress(rawData);
    }

    public Date getTime() throws AvpException {
        return AvpTypeParser.bytesToDate(rawData);
    }

    public String getDiameterIdentity() throws UnsupportedEncodingException {
        return AvpTypeParser.bytesToOctetString(rawData);
    }

    public URI getDiameterURI() throws UnsupportedEncodingException, URISyntaxException {
        return new URI(AvpTypeParser.bytesToOctetString(rawData));
    }

    public int getEnumerated() throws AvpException {
        return AvpTypeParser.bytesToInt32(rawData);
    }

    public void setAvpGrouped(AvpList avpGrouped) {
        this.avpGrouped = avpGrouped;
    }

    public AvpList getAvpGrouped() throws AvpException {
        try {
            if (avpGrouped == null) {
                avpGrouped = AvpParser.decodeAvpGrouped(rawData);
            }
            return avpGrouped;
        } catch (Exception e) {
            throw new AvpException(e, this);
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        append("", sb, this);
        return sb.toString();
    }

    public StringBuilder append(String space, StringBuilder sb, Avp avp) {
        sb.append(space + "AVP: " + avp.name + "(" + avp.code + ") l=" + avp.length + " f=-M-");
        if (avp.avpType != AvpType.Grouped) sb.append(" val=" + avp.getValue());
        sb.append("\n");
        sb.append(space + "AVP Code: " + avp.code + "\n");
        sb.append(space + "AVP Flags: 0x" + Integer.toHexString(avp.flags) + "\n");
        sb.append(space + "AVP Length: " + avp.length + "\n");
        if (avp.isVendorId) sb.append(space + "AVP Vendor Id: " + avp.vendorId + "\n");
        sb.append(space + avp.name + ": " + avp.getValue() + "\n");
        if (avp.avpType == AvpType.Grouped) {
            sb.append("\n");
            space += " ";
            for (Avp a : avp.avpGrouped)
                sb = append(space, sb, a);
        }
        return sb;
    }
}
