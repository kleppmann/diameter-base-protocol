package com.linkedlogics.diameter.object;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shnovruzov on 17/5/2018.
 */
public class Dictionary {

    private Map<Integer, Avp> map;
    private static Dictionary dictionary;

    private Dictionary() {
        map = new HashMap<>();
    }

    public static Dictionary getInstance() {
        if (dictionary == null)
            dictionary = new Dictionary();
        return dictionary;
    }

    public AvpType getDataType(int code) {
        return map.get(code).getAvpType();
    }

    public String getName(int code) {
        return map.get(code).getName();
    }

    public boolean validate(DiameterMessage message) {
        return true;
    }
}
