package com.linkedlogics.diameter.object;

/**
 * Created by shnovruzov on 25/5/2018.
 */
public interface StateEvent {

    public void setType(EventType type);
    public EventType getType();
    public Object getData();
    public void setData(Object data);

}
