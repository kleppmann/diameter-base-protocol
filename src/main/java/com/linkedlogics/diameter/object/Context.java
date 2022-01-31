package com.linkedlogics.diameter.object;


import com.linkedlogics.diameter.exception.*;

import java.io.IOException;

public interface Context {
    void connect() throws IOException;

    void disconnect() throws IOException;

    boolean sendMessage(DiameterMessage message) throws TransportException;

    void sendCER() throws TransportException, ParseException;

    void sendCEA(DiameterMessage cer, int resultCode, String errMessage) throws TransportException, ParseException;

    void sendDWR() throws TransportException, ParseException;

    void sendDWA(DiameterMessage dwr, int resultCode, String errorMessage) throws TransportException, ParseException;

    void sendDPR(int disconnectCause) throws TransportException, ParseException;

    void sendDPA(DiameterMessage dpr, int resultCode, String errorMessage) throws TransportException, ParseException;

    boolean receiveMessage(DiameterMessage message) throws ParseException;

    boolean processCEA(String key, DiameterMessage message)throws ParseException;

    boolean processCER(String key, DiameterMessage message)throws ParseException;

    boolean isConnected();

    String getPeerDescription();
}
