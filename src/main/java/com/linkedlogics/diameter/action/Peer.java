package com.linkedlogics.diameter.action;


import com.linkedlogics.application.logger.Logger;
import com.linkedlogics.application.logger.LoggerLevel;
import com.linkedlogics.diameter.exception.*;
import com.linkedlogics.diameter.fsm.PeerFSM;
import com.linkedlogics.diameter.network.TcpNetwork;
import com.linkedlogics.diameter.object.*;
import com.linkedlogics.diameter.object.Dictionary;
import com.linkedlogics.diameter.parser.MessageParser;
import com.linkedlogics.diameter.utility.UIDGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.jdiameter.api.Avp.ERROR_MESSAGE;

public class Peer {

    protected InetAddress[] addresses;
    protected String realmName;
    protected long vendorID;
    protected String productName;
    protected int firmWare;
    protected AtomicLong hopByHopId = new AtomicLong(new UIDGenerator().nextLong());
    protected PeerFSM fsm;
    protected boolean stopping;
    protected Connection connection;
    protected ConnectionHandler handler;
    protected final Map<Long, DiameterMessage> peerRequests = new ConcurrentHashMap<>();
    protected final static Dictionary dictionary = Dictionary.getInstance();
    protected URI uri;
    protected long originStateId;
    protected Set<ApplicationId> commonApplicationIds = new HashSet<>();
    protected Set<ApplicationId> applicationIds = new HashSet<>();

    public Peer() throws IOException, URISyntaxException {

        this.fsm = new PeerFSM(new PeerContext());

        handler = new ConnectionHandler() {

            @Override
            public void connectionOpened() {
                try {
                    fsm.handleEvent(new FSMEvent(EventType.START_EVENT, null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void messageReceived(DiameterMessage message) {

                if(message.isRequest())System.out.println("request");

                boolean isRequest = message.isRequest();
                try {
                    switch (message.getCode()) {
                        case MessageCode.DEVICE_WATCHDOG_REQUEST:
                            fsm.handleEvent(new FSMEvent(isRequest ? EventType.DWR_EVENT : EventType.DWA_EVENT, message));
                            break;
                        case MessageCode.CAPABILITIES_EXCHANGE_REQUEST:
                            fsm.handleEvent(new FSMEvent(isRequest ? EventType.CER_EVENT : EventType.CEA_EVENT, message));
                            break;
                        case MessageCode.DISCONNECT_PEER_REQUEST:
                            fsm.handleEvent(new FSMEvent(isRequest ? EventType.DPR_EVENT : EventType.DPA_EVENT, message));
                            break;
                        default:
                            fsm.handleEvent(new FSMEvent(EventType.RECEIVE_MSG_EVENT, message));
                            break;
                    }
                } catch (Exception e) {
                    if (isRequest) {
                        try {
                            message.setRequest(false);
                            message.setError(true);
                            message.getAvpList().addAvp(AvpCode.RESULT_CODE, ResultCode.TOO_BUSY, true);
                            connection.sendMessage(message);
                        } catch (Exception ex) {
                            Logger.log(LoggerLevel.ERROR, "Unable to send error message code=%d", message.getCode());
                        }
                    }
                }
            }

            @Override
            public void connectionClosed() {
                try {
                    fsm.handleEvent(new FSMEvent(EventType.DISCONNECT_EVENT, null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        connection = new TcpNetwork();
        connection.init();
        connection.setConnectionHandler(handler);
        originStateId = System.currentTimeMillis();
        realmName = "test";
        productName = "test";
        addresses = new InetAddress[0];
        applicationIds.add(ApplicationId.createByAuthAppId(0L));
    }

    public void connect() throws InternalException, IllegalDiameterStateException, IOException {

        if (fsm.getState() != FSMState.CLOSED) {
            throw new IllegalDiameterStateException("Invalid state: " + fsm.getState());
        }
        try {
            fsm.handleEvent(new FSMEvent(EventType.START_EVENT));
        } catch (InternalException e) {
            throw new InternalException(e);
        }
    }

    public void disconnect(int cause) throws IOException {
        try {
            if (fsm.getState() != FSMState.CLOSED) {
                stopping = true;
                FSMEvent event = new FSMEvent(EventType.STOP_EVENT);
                event.setData(cause);
                fsm.handleEvent(event);
            }
        } catch (Exception e) {
            stopping = false;
        }
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public long getOriginStateId() {
        return originStateId;
    }

    public FSMState getState() {
        return fsm.getState();
    }

    public InetAddress[] getIPAddresses() {
        return addresses;
    }

    public String getRealmName() {
        return realmName;
    }

    public long getVendorId() {
        return vendorID;
    }

    public String getProductName() {
        return productName;
    }

    public long getFirmware() {
        return firmWare;
    }

    public long getHopByHopIdentifier() {
        return hopByHopId.incrementAndGet();
    }

    public Set<ApplicationId> getApplicationIds() {
        return applicationIds;
    }

    public void addMessage(DiameterMessage message) {
        peerRequests.put(message.getHopByHopIdentifier(), message);
    }

    public void remMessage(DiameterMessage message) {
        peerRequests.remove(message.getHopByHopIdentifier());
    }

    public DiameterMessage[] remAllMessage() {
        DiameterMessage[] m = peerRequests.values().toArray(new DiameterMessage[peerRequests.size()]);
        peerRequests.clear();
        return m;
    }

    public boolean handleMessage(EventType type, DiameterMessage message, String key) throws OverloadException, InternalException {
        return !stopping && fsm.handleEvent(new FSMEvent(key, type, message));
    }

    public boolean sendMessage(DiameterMessage message) throws OverloadException, InternalException {
        return dictionary.validate(message) && !stopping && fsm.handleEvent(new FSMEvent(EventType.SEND_MSG_EVENT, message));
    }

    public boolean hasValidConnection() {
        return connection != null && connection.isConnected();
    }

    public void setRealm(String realm) {
        realmName = realm;
    }

    public void setConnectionHandler(ConnectionHandler handler) {
        if (connection != null) {
            connection.setConnectionHandler(handler);
        }
    }

    public boolean isConnected() {
        return getState() == FSMState.OPEN;
    }

    public Set<ApplicationId> getCommonApplicationIds(DiameterMessage message) {

        commonApplicationIds.clear();
        Set<ApplicationId> remoteIds = message.getApplicationIds();
        for (ApplicationId rem : remoteIds)
            commonApplicationIds.addAll(applicationIds.stream().filter(rem::equals).map(loc -> rem).collect(Collectors.toList()));

        return commonApplicationIds;
    }

    protected void sendErrorAnswer(DiameterMessage message, String errorMessage, int resultCode, Avp... avpsToAdd) {
        Logger.log(LoggerLevel.DEBUG, "Could not process request. Result code = %d, error message: %d", resultCode, errorMessage);
        message.setRequest(false);
        message.setError(true);
        message.getAvpList().addAvp(AvpCode.RESULT_CODE, resultCode, true, false, true);

        if (avpsToAdd != null) {
            for (Avp a : avpsToAdd) {
                message.getAvpList().addAvp(a);
            }
        }

        message.getAvpList().removeAvp(AvpCode.ORIGIN_HOST);
        message.getAvpList().removeAvp(AvpCode.ORIGIN_REALM);
        //message.getAvps().addAvp(AvpCode.ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
        //message.getAvps().addAvp(AvpCode.ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
        if (errorMessage != null) {
            message.getAvpList().addAvp(AvpCode.ERROR_MESSAGE, errorMessage, false);
        }

        message.getAvpList().removeAvp(AvpCode.DESTINATION_HOST);
        message.getAvpList().removeAvp(AvpCode.DESTINATION_REALM);
        try {
            Logger.log(LoggerLevel.DEBUG, "Sending response indicating we could not process request");
            sendMessage(message);
        } catch (Exception e) {
            Logger.log(LoggerLevel.ERROR, "Unable to send error answer");
        }
    }

    protected void fillIPAddressTable(DiameterMessage message) {
        AvpList avps = message.getAvpList().getAvps(AvpCode.HOST_IP_ADDRESS);
        if (avps != null) {
            List<InetAddress> list = new ArrayList<InetAddress>();
            for (int i = 0; i < avps.size(); i++) {
                try {
                    list.add(avps.getAvpByIndex(i).getAddress());
                } catch (AvpException e) {
                    Logger.log(LoggerLevel.ERROR, "Unable to retrieve IP Address from Host-IP-Address AVP");
                }
            }
            addresses = list.toArray(new InetAddress[list.size()]);
        }
    }

    protected void addAppId(ApplicationId appId, DiameterMessage message) {
        if (appId.getVendorId() == 0) {
            if (appId.getAuthAppId() != 0) {
                message.getAvpList().addAvp(AvpCode.AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
            } else if (appId.getAcctAppId() != 0) {
                message.getAvpList().addAvp(AvpCode.ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
            }
        } else {
            // Avoid duplicates
            boolean vendorIdPresent = false;
            for (Avp avp : message.getAvpList().getAvps(AvpCode.SUPPORTED_VENDOR_ID)) {
                try {
                    if (avp.getUnsigned32() == appId.getVendorId()) {
                        vendorIdPresent = true;
                        break;
                    }
                } catch (Exception e) {
                    Logger.log(LoggerLevel.ERROR, e, "Failed to read Supported-Vendor-Id.");
                }
            }
            if (!vendorIdPresent) {
                message.getAvpList().addAvp(AvpCode.SUPPORTED_VENDOR_ID, appId.getVendorId(), true, false, true);
            }
            AvpList vendorApp = message.getAvpList().addGroupedAvp(AvpCode.VENDOR_SPECIFIC_APPLICATION_ID, true, false);
            vendorApp.addAvp(AvpCode.VENDOR_ID, appId.getVendorId(), true, false, true);
            if (appId.getAuthAppId() != 0) {
                vendorApp.addAvp(AvpCode.AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
            }
            if (appId.getAcctAppId() != 0) {
                vendorApp.addAvp(AvpCode.ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
            }
        }
    }

    public class PeerContext implements Context {

        @Override
        public void connect() throws IOException {
            try {
                connection.connect();
                uri = new URI(connection.getRemoteAddress().toString());
                Logger.log(LoggerLevel.DEBUG, "Connected to peer %s", connection.getRemoteAddress());
            } catch (IOException | URISyntaxException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void disconnect() throws IOException {
            try {
                connection.disconnect();
                Logger.log(LoggerLevel.DEBUG, "Disconnected from peer %s", connection.getRemoteAddress());
            } catch (IOException e) {
                throw new IOException(e);
            }
        }

        @Override
        public boolean sendMessage(DiameterMessage message) throws TransportException {

//            if (message.isTimeOut()) {
//                Logger.log(LoggerLevel.DEBUG, "Message is timeout %s", message);
//                return false;
//            }

            if (message.getState() == DiameterMessage.SENT) {
                Logger.log(LoggerLevel.DEBUG, "Message already sent %s", message);
                return false;
            }

            if (!message.isRequest()) {
                message.getAvpList().removeAvp(AvpCode.DESTINATION_HOST);
                message.getAvpList().removeAvp(AvpCode.DESTINATION_REALM);

//                int commandCode = message.getCode();
//                // We don't want this for CEx/DWx/DPx
//                if(commandCode != 257 && commandCode != 280 && commandCode != 282) {
//                    if(table instanceof MutablePeerTableImpl) { // available only to server, client skip this step
//                        MutablePeerTableImpl peerTable = (MutablePeerTableImpl) table;
//                        if(peerTable.isDuplicateProtection()) {
//                            String[] originInfo = router.getRequestRouteInfo(message);
//                            if(originInfo != null) {
//                                // message.getDuplicationKey() doesn't work because it's answer
//                                peerTable.saveToDuplicate(message.getDuplicationKey(originInfo[0], message.getEndToEndIdentifier()), message);
//                            }
//                        }
//                    }
//                }
            }
            // PCB added this
            //router.garbageCollectRequestRouteInfo(message);

            message.setState(DiameterMessage.SENT);
            boolean res = connection.sendMessage(message);
            //Logger.log(LoggerLevel.DEBUG, "Sending message %s to peer %s", message, uri);
            return res;
        }

        @Override
        public void sendCER() throws TransportException {
            DiameterMessage message = new DiameterMessage(MessageCode.CAPABILITIES_EXCHANGE_REQUEST);
            message.setRequest(true);
            message.setHopByHopIdentifier(hopByHopId.incrementAndGet());

            message.getAvpList().addAvp(AvpCode.ORIGIN_HOST, uri.toString(), true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_REALM, realmName, true, false, true);
            for (InetAddress ia : getIPAddresses()) {
                message.getAvpList().addAvp(AvpCode.HOST_IP_ADDRESS, ia, true, false);
            }

            message.getAvpList().addAvp(AvpCode.AUTH_APPLICATION_ID, 0L, true, false, true);
            //for (ApplicationId appId : getApplicationIds()) {
                //addAppId(appId, message);
            //}

            message.getAvpList().addAvp(AvpCode.VENDOR_ID, vendorID, true, false, true);
            message.getAvpList().addAvp(AvpCode.PRODUCT_NAME, productName, false);

            message.getAvpList().addAvp(AvpCode.FIRMWARE_REVISION, firmWare, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_STATE_ID, originStateId, true, false, true);
            sendMessage(message);
        }

        @Override
        public void sendCEA(DiameterMessage cer, int resultCode, String errMessage) throws TransportException {
//            DiameterMessage message = new DiameterMessage(MessageCode.CAPABILITIES_EXCHANGE_ANSWER);
//            message.setRequest(false);
//            message.setHopByHopIdentifier(hopByHopId.incrementAndGet());
//
//            message.getAvps().addAvp(AvpCode.ORIGIN_HOST, uri.toString(), true, false, true);
//            message.getAvps().addAvp(AvpCode.ORIGIN_REALM, realmName, true, false, true);
//            for (InetAddress ia : getIPAddresses()) {
//                message.getAvps().addAvp(AvpCode.HOST_IP_ADDRESS, ia, true, false);
//            }
//
//            message.getAvps().addAvp(AvpCode.VENDOR_ID, vendorID, true, false, true);
//            message.getAvps().addAvp(AvpCode.PRODUCT_NAME, productName, false);
//
//            message.getAvps().addAvp(AvpCode.FIRMWARE_REVISION, firmWare, true);
//            message.getAvps().addAvp(AvpCode.ORIGIN_STATE_ID, originStateId, true, false, true);
//            sendMessage(message);
        }

        @Override
        public void sendDWR() throws TransportException, ParseException {
            Logger.log(LoggerLevel.DEBUG, "Sending DWR message\n");
            DiameterMessage message = new DiameterMessage(MessageCode.DEVICE_WATCHDOG_REQUEST);
            message.setRequest(true);
            message.setHopByHopIdentifier(getHopByHopIdentifier());

            message.getAvpList().addAvp(AvpCode.ORIGIN_HOST, uri.toString(), true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_REALM, realmName, true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_STATE_ID, originStateId, true, false, true);

            message.getAvpList().removeAvp(AvpCode.DESTINATION_HOST);
            message.getAvpList().removeAvp(AvpCode.DESTINATION_REALM);
            sendMessage(message);
        }

        @Override
        public void sendDWA(DiameterMessage dwr, int resultCode, String errorMessage) throws ParseException, TransportException {
            DiameterMessage message = MessageParser.createMessage(dwr);
            message.setRequest(false);
            message.setHopByHopIdentifier(dwr.getHopByHopIdentifier());
            message.setEndToEndIdentifier(dwr.getEndToEndIdentifier());

            message.getAvpList().addAvp(AvpCode.RESULT_CODE, resultCode, true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_HOST, uri.toString(), true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_REALM, realmName, true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_STATE_ID, originStateId, true, false, true);
            if (errorMessage != null) {
                message.getAvpList().addAvp(AvpCode.ERROR_MESSAGE, errorMessage, false);
            }
            message.getAvpList().removeAvp(AvpCode.DESTINATION_HOST);
            message.getAvpList().removeAvp(AvpCode.DESTINATION_REALM);
        }

        @Override
        public void sendDPR(int disconnectCause) throws ParseException, TransportException {
            Logger.log(LoggerLevel.DEBUG, "Send DPR message with Disconnect-Cause %d", disconnectCause);
            DiameterMessage message = MessageParser.createMessage(MessageCode.DISCONNECT_PEER_REQUEST, 0);
            message.setRequest(true);
            message.setHopByHopIdentifier(getHopByHopIdentifier());
            message.getAvpList().addAvp(AvpCode.ORIGIN_HOST, uri.toString(), true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_REALM, realmName, true, false, true);
            message.getAvpList().addAvp(AvpCode.DISCONNECT_CAUSE, disconnectCause, true, false);
            sendMessage(message);
        }

        @Override
        public void sendDPA(DiameterMessage dpr, int resultCode, String errorMessage) throws ParseException, TransportException {
            Logger.log(LoggerLevel.DEBUG, "Send DPA message");
            DiameterMessage message = MessageParser.createMessage(dpr);
            message.setRequest(false);
            message.setHopByHopIdentifier(dpr.getHopByHopIdentifier());
            message.setEndToEndIdentifier(dpr.getEndToEndIdentifier());
            message.getAvpList().addAvp(AvpCode.RESULT_CODE, resultCode, true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_HOST, uri.toString(), true, false, true);
            message.getAvpList().addAvp(AvpCode.ORIGIN_REALM, realmName, true, false, true);
            if (errorMessage != null) {
                message.getAvpList().addAvp(ERROR_MESSAGE, errorMessage, false);
            }
            sendMessage(message);
        }

        @Override
        public boolean processCER(String key, DiameterMessage message) throws ParseException {
            return true;
        }

        @Override
        public boolean processCEA(String key, DiameterMessage message) throws ParseException {

            try {
                Avp origHost = message.getAvpList().getAvp(AvpCode.ORIGIN_HOST);
                Avp origRealm = message.getAvpList().getAvp(AvpCode.ORIGIN_REALM);
                Avp vendorId = message.getAvpList().getAvp(AvpCode.VENDOR_ID);
                Avp prdName = message.getAvpList().getAvp(AvpCode.PRODUCT_NAME);
                Avp resCode = message.getAvpList().getAvp(AvpCode.RESULT_CODE);
                Avp frmId = message.getAvpList().getAvp(AvpCode.FIRMWARE_REVISION);
                AvpList hostIp = message.getAvpList().getAvps(AvpCode.HOST_IP_ADDRESS);

                if (origHost == null || origRealm == null || vendorId == null
                        || prdName == null || resCode == null || hostIp == null) {
                    Logger.log(LoggerLevel.DEBUG, "Missing avp in CEA");
                    return false;
                }

                int code = resCode.getInteger32();
                if (code != ResultCode.SUCCESS) {
                    Logger.log(LoggerLevel.ERROR, "Result code value %d", code);
                    return false;
                }

                commonApplicationIds = getCommonApplicationIds(message);
                if (commonApplicationIds.isEmpty()) {
                    Logger.log(LoggerLevel.ERROR, "CEA did not contained common appIds");
                    return false;
                }

                if (realmName == null) {
                    realmName = origRealm.getDiameterIdentity();
                }
                if (vendorID == 0) {
                    vendorID = vendorId.getUnsigned32();
                }

                fillIPAddressTable(message);

                if (productName == null) {
                    productName = prdName.getUTF8String();
                }

                if (firmWare == 0 && frmId != null) {
                    firmWare = frmId.getInteger32();
                }

            } catch (Exception e) {
                Logger.log(LoggerLevel.ERROR, e, "Unable to process CEA message");
                return false;
            }
            return true;
        }

        @Override
        public boolean receiveMessage(DiameterMessage message) throws ParseException {
            return true;
        }

        @Override
        public boolean isConnected() {
            return connection != null && connection.isConnected();
        }

        @Override
        public String getPeerDescription() {
            return null;
        }

    }

}
