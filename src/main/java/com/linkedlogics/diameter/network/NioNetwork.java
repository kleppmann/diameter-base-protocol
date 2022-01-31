package com.linkedlogics.diameter.network;

import com.linkedlogics.application.exception.ExceptionUtility;
import com.linkedlogics.application.logger.Logger;
import com.linkedlogics.application.logger.LoggerLevel;
import com.linkedlogics.application.property.Property;
import com.linkedlogics.application.utility.ParameterUtility;
import com.linkedlogics.diameter.exception.ParseException;
import com.linkedlogics.diameter.exception.TransportException;
import com.linkedlogics.diameter.network.selector.SelectorThread;
import com.linkedlogics.diameter.network.selector.WriterThread;
import com.linkedlogics.diameter.object.Connection;
import com.linkedlogics.diameter.object.ConnectionHandler;
import com.linkedlogics.diameter.object.DiameterMessage;
import com.linkedlogics.diameter.parser.MessageParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class NioNetwork implements Connection {
    protected HashMap<String, Object> properties = new HashMap<>();

    protected boolean isConnected;
    protected String localHost;
    protected int localPort;
    protected String remoteHost;
    protected int remotePort;
    protected String name;
    protected ByteBuffer writerBuffer;
    protected ByteBuffer readerBuffer;
    protected long sendBuffer;
    protected long recvBuffer;
    protected boolean noDelay;
    protected NetworkProtocol protocol;
    protected ArrayList<Payload> payloads = new ArrayList<>();
    protected Queue<DiameterMessage> messageQueue = new ConcurrentLinkedQueue<>();

    protected SelectorThread selectorThread;
    protected WriterThread writerThread;
    protected ConnectionHandler handler;

    public NioNetwork(NetworkProtocol protocol) {
        properties.put(SEND_BUFFER_SIZE, 0);
        properties.put(RECV_BUFFER_SIZE, 0);
        properties.put(LOCAL_HOST, "172.18.201.122");
        properties.put(LOCAL_PORT, 5001);
        properties.put(REMOTE_HOST, "172.18.188.103");
        properties.put(REMOTE_PORT, 3971);
        properties.put(PROCESSOR_BUFFER_SIZE, 8192);
        properties.put(NO_DELAY, true);
        this.protocol = protocol;
    }

    public boolean init() {

        sendBuffer = 0;
        recvBuffer = 0;
        noDelay = ParameterUtility.getBoolean(NO_DELAY, properties, this.getClass());
        writerBuffer = ByteBuffer.allocate(ParameterUtility.getInt(PROCESSOR_BUFFER_SIZE, properties, this.getClass()));
        readerBuffer = ByteBuffer.allocate(ParameterUtility.getInt(PROCESSOR_BUFFER_SIZE, properties, this.getClass()));


        this.localHost = ParameterUtility.getStringOrDefault(LOCAL_HOST, properties, null);
        this.localPort = ParameterUtility.getIntOrDefault(LOCAL_PORT, properties, 0);
        this.remoteHost = ParameterUtility.getStringOrDefault(REMOTE_HOST, properties, null);
        this.remotePort = ParameterUtility.getIntOrDefault(REMOTE_PORT, properties, 0);
        if (name == null) {
            this.name = localHost + ":" + localPort;
        }

        return true;

    }

    public abstract void finishConnect(SelectionKey key);

    public abstract void finishAccept(SelectionKey key);

    public abstract void setChannel(SocketChannel tcpChannel) throws IOException;

    public abstract AbstractSelectableChannel getChannel();

    public abstract Payload[] readFromChannel() throws IOException;

    public abstract void startServer() throws IOException;

    public abstract void connect() throws IOException;

    public abstract void disconnect();

    protected NetworkProtocol getProtocol() {
        return protocol;
    }

    public void read(SelectionKey key) {

        try {
            Payload[] payloads = readFromChannel();
            if (payloads != null && payloads.length > 0) {
                for (int i = 0; i < payloads.length; i++) {
                    handler.messageReceived(MessageParser.decodeDiameterMessage(payloads[i].getData()));
                }
            }
        } catch (Exception e) {
            if (key != null)
                key.cancel();
            ExceptionUtility.handleException(e);
        }
    }

    public int write() throws IOException, ParseException {
        return writeToChannel();
    }

    public abstract int writeToChannel(java.nio.ByteBuffer buffer, DiameterMessage message) throws IOException;

    protected int writeToChannel() throws IOException, ParseException {

        int sentCount = 0;
        DiameterMessage message = poll();
        while (message != null) {
            try {
                byte[] bytes = MessageParser.encodeMessage(message);
                writerBuffer.put(bytes);
                long start = System.currentTimeMillis();
                int sentBytes = writeToChannel(writerBuffer, message);
                long end = System.currentTimeMillis();
                if (writerBuffer.hasRemaining()) {
                    writerBuffer.compact();
                } else {
                    writerBuffer.clear();
                }
                if (end - start > 30) {
                    Logger.log(LoggerLevel.WARN, "performance network %s (protocol=%s) %s:%d to %s:%d writing delayed for %d msec", name, getProtocol(), localHost, localPort, remoteHost, remotePort, (end - start));
                }
            } catch (Exception e) {
                ExceptionUtility.handleException(e);
                writerBuffer.clear();
                throw e;
            }
            message = messageQueue.poll();
        }

        return sentCount;
    }

    public boolean offer(DiameterMessage message) {
        return messageQueue.offer(message);
    }

    public DiameterMessage poll() {
        return messageQueue.poll();
    }

    public boolean sendMessage(DiameterMessage message) {
        return offer(message);
    }

    public void setConnectionHandler(ConnectionHandler handler) {
        this.handler = handler;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Property(type = "String")
    public static final String LOCAL_HOST = "local_host";
    @Property(type = "Integer")
    public static final String LOCAL_PORT = "local_port";
    @Property(type = "String")
    public static final String REMOTE_HOST = "remote_host";
    @Property(type = "Integer")
    public static final String REMOTE_PORT = "remote_port";
    @Property(type = "Integer", value = "0")
    public static final String SEND_BUFFER_SIZE = "send_buffer_size";
    @Property(type = "Integer", value = "0")
    public static final String RECV_BUFFER_SIZE = "recv_buffer_size";
    @Property(type = "Boolean", value = "true")
    public static final String NO_DELAY = "no_delay";
    @Property(type = "Integer", value = "8192")
    public static final String PROCESSOR_BUFFER_SIZE = "processor_buffer_size";
}
