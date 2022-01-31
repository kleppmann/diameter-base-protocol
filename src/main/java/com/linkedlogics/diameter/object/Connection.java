package com.linkedlogics.diameter.object;


import com.linkedlogics.diameter.exception.ParseException;
import com.linkedlogics.diameter.exception.TransportException;

import java.io.IOException;
import java.net.InetAddress;

public interface Connection {

    boolean init();

    void connect() throws IOException;

    void disconnect() throws IOException;

    boolean sendMessage(DiameterMessage message);

    boolean isConnected();

    InetAddress getRemoteAddress();

    void setConnectionHandler(ConnectionHandler handler);

}
