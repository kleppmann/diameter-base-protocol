package com.linkedlogics.diameter.object;

/**
 * Created by shnovruzov on 25/5/2018.
 */
public class FSMState {

    private int code;
    private String name;

    public static final FSMState OPEN = new FSMState(0, "OPEN");
    public static final FSMState SUSPECT = new FSMState(1, "SUSPECT");
    public static final FSMState CLOSED = new FSMState(2, "CLOSED");
    public static final FSMState WAITING = new FSMState(3, "WAITING");
    public static final FSMState CLOSING = new FSMState(4, "OPEN");

    public FSMState(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

}
