package com.linkedlogics.diameter.network;

import com.linkedlogics.application.exception.ExceptionUtility;
import com.linkedlogics.application.logger.Logger;
import com.linkedlogics.application.logger.LoggerLevel;
import com.linkedlogics.application.property.Property;
import com.linkedlogics.application.utility.ParameterUtility;
import com.linkedlogics.diameter.network.selector.ChangeRequest;
import com.linkedlogics.diameter.network.selector.ThreadFactory;
import com.linkedlogics.diameter.object.ConnectionHandler;
import com.linkedlogics.diameter.object.DiameterMessage;
import com.linkedlogics.diameter.parser.MessageParser;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.HashMap;
import java.util.Map.Entry;

public class TcpServerNetwork extends NioNetwork {
    protected ServerSocketChannel tcpServerChannel;
    //protected HashMap<String, TcpNetwork> channels;
    TcpNetwork tcpNetwork;
    ConnectionHandler handler;

    public TcpServerNetwork() {
        super(NetworkProtocol.tcp);
    }

    @Override
    public boolean init() {
        //channels = new HashMap<String, TcpNetwork>();
//        if (super.init()) {
//            return true;
//        }
//        return false;
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

        selectorThread = ThreadFactory.getServerSelectorThread();
        writerThread = ThreadFactory.getServerWriterThread();
        writerThread.addNetwork(this);

        return true;
    }

    @Override
    public void finishAccept(SelectionKey key) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = tcpServerChannel.accept();
            SocketAddress socketAddress = socketChannel.getRemoteAddress();

            setChannel(socketChannel);
            Logger.log(LoggerLevel.INFO, "connection network %s (protocol=%s) %s:%d from %s:%d is accepted", name, getProtocol(), localHost, localPort, ((InetSocketAddress) socketAddress).getAddress().getHostAddress(), ((InetSocketAddress) socketAddress).getPort());
            return;
        } catch (IOException e) {
            ExceptionUtility.handleException(e);
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    @Override
    public void startServer() throws IOException {
        long start = System.currentTimeMillis();
        Logger.log(LoggerLevel.INFO, "starting network %s (protocol=%s) as server %s:%d", name, getProtocol(), localHost, localPort);
        try {
            tcpServerChannel = ServerSocketChannel.open();
            tcpServerChannel.configureBlocking(false);
            tcpServerChannel.bind(new InetSocketAddress(localHost, localPort));
            selectorThread.handleChange(new ChangeRequest(tcpServerChannel, ChangeRequest.REGISTER, SelectionKey.OP_ACCEPT, this));
            Logger.log(LoggerLevel.INFO, "started  network %s in %d msec", name, (System.currentTimeMillis() - start));
        } catch (BindException e) {
            ExceptionUtility.handleException(e, "starting network %s is failed %s:%d because address is already in use", name, localHost, localPort);
            throw e;
        }
    }

    @Override
    public AbstractSelectableChannel getChannel() {
        return null;
    }

    @Override
    public void finishConnect(SelectionKey key) {

    }

    @Override
    public boolean isConnected() {
        return tcpNetwork != null;
    }

    public void read(SelectionKey key) {
        //TcpNetwork network = channels.get(getChannelKey((SocketChannel) key.channel()));
        try {
            Payload[] payloads = tcpNetwork.readFromChannel();
            Logger.log(LoggerLevel.DEBUG, "readed by server");
            for (int i = 0; i < payloads.length; i++) {
                DiameterMessage message = MessageParser.decodeDiameterMessage(payloads[i].getData());
                System.out.println(message.getCode());
                message.setRequest(false);
                offer(message);
                //getConnection().getNode().getProcessorEnvironment().offer(IamProcessorLogic.PROCESSOR_NAME, payloads[i]) ;
            }
        } catch (Exception e) {
            if (key != null)
                key.cancel();
            ExceptionUtility.handleException(e);
            //network.getNetworkStateManager().handleEvent(new ConnectionFailed());
        }
    }

    @Override
    public boolean offer(DiameterMessage message) {
        //TcpNetwork network = channels.get(message.getHost() + ":" + message.getPort());
        return tcpNetwork.offer(message);
    }

    @Override
    public Payload[] readFromChannel() throws IOException {
        return null;
    }

    @Override
    public int writeToChannel(java.nio.ByteBuffer buffer, DiameterMessage message) throws IOException {
        return 0;
    }

    @Override
    public int write() throws IOException {
        return 0;
    }

    @Override
    public void connect() throws IOException {

    }

    @Override
    public void disconnect() {
        tcpNetwork.disconnect();
//        for (Entry<String, TcpNetwork> network : channels.entrySet()) {
//            network.getValue().disconnect();
//        }
    }

    public void setChannel(SocketChannel tcpChannel) throws IOException {
        SocketAddress socketAddress = tcpChannel.getRemoteAddress();
        String host = ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
        int port = ((InetSocketAddress) socketAddress).getPort();

        TcpNetwork network = new TcpNetwork();
        if (network.init()) {
            network.setChannel(tcpChannel, false);
            network.setConnected(true);
            //channels.put(host + ":" + port, network);
            this.tcpNetwork = network;
        }
        tcpChannel.register(selectorThread.getSelector(), SelectionKey.OP_READ, this);
    }

    public String getChannelKey(SocketChannel socketChannel) {
        try {
            SocketAddress socketAddress = socketChannel.getRemoteAddress();
            String host = ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
            int port = ((InetSocketAddress) socketAddress).getPort();
            return host + ":" + port;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public InetAddress getRemoteAddress() {
        try {
            SocketChannel channel = (SocketChannel) tcpNetwork.getChannel();
            SocketAddress socketAddress = channel.getRemoteAddress();
            InetSocketAddress address = (InetSocketAddress) socketAddress;
            return address.getAddress();
        } catch (IOException e) {
            return null;
        }
    }

    @Property(type = "Integer", value = "1")
    public static final String SCATTER = "scatter";

    @Property(type = "Integer", flags = "F")
    public static final String CHANNELS = "channels";
}
