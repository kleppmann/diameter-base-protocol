package com.linkedlogics.diameter.object;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by shnovruzov on 19/5/2018.
 */
public class AvpDefinition {

    private int code;
    private String name;
    private long vendorId;
    private boolean isVendorId;
    private boolean isMandatory;
    private boolean isProtected;
    private boolean isEncrypted;
    private AvpType avpType;
    private List<AvpDefinition> avps;
    private List<EnumDefinition> enums;

    public AvpDefinition() {

    }

    public int getCode() {
        return code;
    }

    public long getVendorId() {
        return vendorId;
    }

    public AvpType getAvpType() {
        return avpType;
    }

    public String getName() {
        return name;
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

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public List<AvpDefinition> getAvps() {
        if (avps == null) return new ArrayList<>();
        return avps;
    }

    public List<EnumDefinition> getEnums() {
        if (enums == null) return new ArrayList<>();
        return enums;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVendorId(boolean vendorId) {
        isVendorId = vendorId;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public void setAvpType(AvpType avpType) {
        this.avpType = avpType;
    }

    public void setAvps(List<AvpDefinition> avps) {
        this.avps = avps;
    }

    public void setEnums(List<EnumDefinition> enums) {
        this.enums = enums;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        append("", sb, this);
        return sb.toString();
    }

    public StringBuilder append(String space, StringBuilder sb, AvpDefinition avp) {
        sb.append(space + "<avp name=" + avp.name + " code=" + avp.code + ">\n");
        if (avp.avpType != AvpType.Grouped) {
            sb.append(space + "  <type  type-name=" + avp.getAvpType() + "/>\n");
            if (avp.avpType == AvpType.Enumerated) {
                for (EnumDefinition e : avp.getEnums())
                    sb.append(space + "    " + e.toString() + "\n");
                sb.append(space + "  </type>\n");
            }
        } else {
            sb.append(space + "  <grouped>\n");
            for (AvpDefinition a : avp.avps)
                sb = append(space + "    ", sb, a);
            sb.append(space + "  </grouped>\n");
        }
        sb.append(space + "</avp>\n");
        return sb;
    }
}
