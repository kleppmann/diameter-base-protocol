package com.linkedlogics.diameter.data;

import com.linkedlogics.diameter.object.AvpDefinition;
import com.linkedlogics.diameter.object.MessageDefinition;

/**
 * Created by shnovruzov on 19/5/2018.
 */
public interface AvpAdapter {

    void init();
    AvpDefinition getAvp(int code);
    AvpDefinition getAvp(int code, long vendorId);
    MessageDefinition getMessage(int code, int isRequest);

}
