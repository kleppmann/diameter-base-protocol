package com.linkedlogics.diameter.data;

import com.linkedlogics.diameter.object.AvpDefinition;
import com.linkedlogics.diameter.object.MessageDefinition;

/**
 * Created by shnovruzov on 19/5/2018.
 */
public class AvpManager {

    private AvpAdapter adapter;
    private static AvpManager manager;

    private AvpManager() {
        adapter = com.linkedlogics.diameter.data.file.AvpAdapter.getInstance();
        adapter.init();
    }

    public static AvpManager getInstance() {
        if (manager == null)
            manager = new AvpManager();
        return manager;
    }

    public AvpAdapter getAvpAdapter() {
        return adapter;
    }

    public AvpDefinition getAvp(int code) {
        return adapter.getAvp(code);
    }

    public AvpDefinition getAvp(int code, long vendorId){
        return adapter.getAvp(code, vendorId);
    }
    public MessageDefinition getMessage(int code, int isRequest){
        return adapter.getMessage(code, isRequest);
    }

}
