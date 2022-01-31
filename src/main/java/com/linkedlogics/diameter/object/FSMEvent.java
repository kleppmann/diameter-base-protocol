package com.linkedlogics.diameter.object;

/**
 * Created by shnovruzov on 25/5/2018.
 */
public class FSMEvent implements StateEvent {

    private String key;
    private Object data;
    private EventType type;
    private final long createdTime = System.currentTimeMillis();

    public FSMEvent(EventType type) {
        this.type = type;
    }

    public FSMEvent(EventType type, DiameterMessage data) {
        this(type);
        this.data = data;
    }

    public FSMEvent(String key, EventType type, DiameterMessage data) {
        this(type, data);
        this.key = key;
    }

    @Override
    public void setType(EventType type) {
        this.type = type;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public DiameterMessage getMessage() {
        return (DiameterMessage) data;
    }

    public String getKey() {
        return key;
    }

    public String toString() {
        return type.name();
    }
}
