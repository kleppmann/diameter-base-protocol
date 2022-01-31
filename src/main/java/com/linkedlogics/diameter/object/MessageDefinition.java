package com.linkedlogics.diameter.object;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by shnovruzov on 19/5/2018.
 */
public class MessageDefinition {

    private int code;
    private String name;
    private long applicationId;
    private boolean isRequest;
    private List<AvpDefinition> avps;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequest(boolean isRequest) {
        this.isRequest = isRequest;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public void setAvps(List<AvpDefinition> avps) {
        this.avps = avps;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public List<AvpDefinition> getAvps() {
        if (avps == null) avps = new ArrayList<>();
        return avps;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<command name=" + name + " code=" + code + " request=" + isRequest + ">\n");
        for (AvpDefinition avp : avps)
            sb.append("  <avp name=" + avp.getName() + " code=" + avp.getCode() + " vendorId=" + avp.getVendorId() + "/>\n");
        sb.append("</command>\n");
        return sb.toString();
    }


}
