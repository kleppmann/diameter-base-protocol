package com.linkedlogics.diameter.network;


public class Payload {
    private byte[] data;

    public Payload() {

    }

    public Payload(byte[] data) {
        this.data = data;

    }

    public byte[] getData() {
        return data;
    }

}
