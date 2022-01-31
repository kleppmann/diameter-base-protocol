package com.linkedlogics.diameter.object;

/**
 * Created by shnovruzov on 25/5/2018.
 */
public interface State {

    public void entryAction();
    public boolean defaultAction(StateEvent event);
    public void exitAction();

}
