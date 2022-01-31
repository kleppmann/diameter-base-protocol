package com.linkedlogics.diameter.object;

import com.linkedlogics.diameter.parser.MessageParser;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shnovruzov on 16/5/2018.
 */
public class DiameterMessage implements Serializable {

    public static final int VERSION = 1;
    public static final int NOT_SENT = 0;
    public static final int SENT = 1;
    public static final int ANSWERED = 2;

    private String host;
    private int port;

    private short version = 1, flags;
    private int code;
    private long applicationId;
    private boolean isRequest = false;
    private boolean isProxiable = false;
    private boolean isError = false;
    private boolean isTransmitted = false;
    private long hopByHopIdentifier, endToEndIdentifier;
    private AvpList avpList;
    private byte[] rawData;

    private int state;
    transient Set<ApplicationId> applicationIds;

    public DiameterMessage(byte[] rawData) {
        this.rawData = rawData;
    }

    public DiameterMessage(int code, long appId) {
        this.code = code;
        this.applicationId = appId;

        this.avpList = new AvpList();
        this.endToEndIdentifier = MessageParser.endToEndGen.nextLong();
    }

    public DiameterMessage(int code) {
        this.code = code;
    }

    public DiameterMessage(int code, short flags, long applicationId, long hopByHopIdentifier, long endToEndIdentifier, AvpList avpList) {
        this.code = code;
        this.flags = flags;
        this.applicationId = applicationId;
        this.hopByHopIdentifier = hopByHopIdentifier;
        this.endToEndIdentifier = endToEndIdentifier;
        this.isRequest = (flags & 0x80) != 0;
        this.isProxiable = (flags & 0x40) != 0;
        this.isError = (flags & 0x20) != 0;
        this.isTransmitted = (flags & 0x10) != 0;
        this.avpList = avpList;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public void setFlags(short flags) {
        this.flags = flags;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public void setHopByHopIdentifier(long hopByHopIdentifier) {
        this.hopByHopIdentifier = hopByHopIdentifier;
    }

    public void setEndToEndIdentifier(long endToEndIdentifier) {
        this.endToEndIdentifier = endToEndIdentifier;
    }

    public void setAvpList(AvpList avpList) {
        this.avpList = avpList;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public short getVersion() {
        return version;
    }

    public int getCode() {
        return code;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public int getFlags() {
        return flags;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean b) {
        if (b) flags |= 0x80;
        else flags &= 0x7F;
    }

    public boolean isProxiable() {
        return isProxiable;
    }

    public void setProxiable(boolean b) {
        if (b) flags |= 0x40;
        else flags &= 0xBF;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean b) {
        if (b) flags |= 0x20;
        else flags &= 0xDF;
    }

    public boolean isTransmitted() {
        return isTransmitted;
    }

    public void setTransmitted(boolean b) {
        if (b) flags |= 0x10;
        else flags &= 0xEF;
    }

    public long getHopByHopIdentifier() {
        return hopByHopIdentifier;
    }

    public long getEndToEndIdentifier() {
        return endToEndIdentifier;
    }

    public AvpList getAvpList() {
        if (avpList == null) avpList = new AvpList();
        return avpList;
    }

    public void addAvp(Avp avp) {

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Version: 0x" + Integer.toHexString(version) + "\n");
        //sb.append("Length :" + length + "\n");
        sb.append("Flags: 0x" + Integer.toHexString(flags) + "\n");
        sb.append("Command Code: " + code + "\n");
        sb.append("Application Id: " + applicationId + "\n");
        sb.append("Hop-by-Hop Identifier: " + hopByHopIdentifier + "\n");
        sb.append("End-to-End Identifier: " + endToEndIdentifier + "\n");
        sb.append("Avp ...\n");
        for (Avp avp : getAvpList()) {
            sb.append("\n");
            sb.append(avp.toString());
        }
        return sb.toString();
    }

    public Set<ApplicationId> getApplicationIds() {

        if (this.applicationIds != null) {
            return this.applicationIds;
        }

        Set<ApplicationId> idList = new HashSet<>();
        try {
            AvpList authAppId = avpList.getAvps(AvpCode.AUTH_APPLICATION_ID);
            for (Avp anAuthAppId : authAppId) {
                idList.add(ApplicationId.createByAuthAppId((anAuthAppId).getUnsigned32()));
            }
            AvpList accAppId = avpList.getAvps(AvpCode.ACCT_APPLICATION_ID);
            for (Avp anAccAppId : accAppId) {
                idList.add(ApplicationId.createByAccAppId((anAccAppId).getUnsigned32()));
            }
            AvpList specAppIds = avpList.getAvps(AvpCode.VENDOR_SPECIFIC_APPLICATION_ID);
            for (Avp specAppId : specAppIds) {
                long vendorId = 0, acctApplicationId = 0, authApplicationId = 0;
                AvpList avps = (specAppId).getAvpGrouped();
                for (Avp localAvp : avps) {
                    if (localAvp.getCode() == AvpCode.VENDOR_ID) {
                        vendorId = localAvp.getUnsigned32();
                    }
                    if (localAvp.getCode() == AvpCode.AUTH_APPLICATION_ID) {
                        authApplicationId = localAvp.getUnsigned32();
                    }
                    if (localAvp.getCode() == AvpCode.ACCT_APPLICATION_ID) {
                        acctApplicationId = localAvp.getUnsigned32();
                    }
                }

                if (authApplicationId != 0) {
                    idList.add(ApplicationId.createByAuthAppId(vendorId, authApplicationId));
                }
                if (acctApplicationId != 0) {
                    idList.add(ApplicationId.createByAccAppId(vendorId, acctApplicationId));
                }
            }
        } catch (Exception exception) {
            return new HashSet<>();
        }

        this.applicationIds = idList;
        return this.applicationIds;
    }
}
