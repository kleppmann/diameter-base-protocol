package com.linkedlogics.diameter.object;


import com.linkedlogics.diameter.exception.TransportException;

import java.io.IOException;
import java.net.InetAddress;

public interface MessageCode {

    int ABORT_SESSION_REQUEST = 274;
    int ABORT_SESSION_ANSWER = 274;
    int ACCOUNTING_REQUEST = 271;
    int ACCOUNTING_ANSWER = 271;
    int CAPABILITIES_EXCHANGE_REQUEST = 257;
    int CAPABILITIES_EXCHANGE_ANSWER = 257;
    int DEVICE_WATCHDOG_REQUEST = 280;
    int DEVICE_WATCHDOG_ANSWER = 280;
    int DISCONNECT_PEER_REQUEST = 282;
    int DISCONNECT_PEER_ANSWER = 282;
    int RE_AUTH_REQUEST = 258;
    int RE_AUTH_ANSWER = 258;
    int SESSION_TERMINATION_REQUEST = 275;
    int SESSION_TERMINATION_ANSWER = 275;
}
