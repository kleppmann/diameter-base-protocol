package com.linkedlogics.diameter.object;

/**
 * Created by shnovruzov on 16/5/2018.
 */
public class EnumDefinition {

    private long code;
    private String name;

    public EnumDefinition(long code, String name) {
        this.code = code;
        this.name = name;
    }

    public long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "<enum code=" + code + " name=" + name + "/>";
    }

}
