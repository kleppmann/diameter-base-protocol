package com.linkedlogics.diameter.object;


public interface ConnectionHandler {

    void connectionOpened();

    void messageReceived(DiameterMessage message);

    void connectionClosed();

}
