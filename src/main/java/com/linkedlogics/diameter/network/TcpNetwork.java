package com.linkedlogics.diameter.network;

import com.linkedlogics.application.exception.ExceptionUtility;
import com.linkedlogics.application.logger.Logger;
import com.linkedlogics.application.logger.LoggerLevel;
import com.linkedlogics.application.utility.ParameterUtility;
import com.linkedlogics.diameter.network.selector.ThreadFactory;
import com.linkedlogics.diameter.object.ConnectionHandler;
import com.linkedlogics.diameter.object.DiameterMessage;
import com.linkedlogics.diameter.network.selector.ChangeRequest;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;


public class TcpNetwork extends NioNetwork {

    protected ServerSocketChannel tcpServerChannel;
    protected SocketChannel tcpChannel;
    protected IamByteBuffer messageBuffer; // used as intermediate buffer

    public TcpNetwork() {
        super(NetworkProtocol.tcp);
    }

    public boolean init() {
        if (super.init()) {
            localPort = 0;
            messageBuffer = new IamByteBuffer(ParameterUtility.getInt(PROCESSOR_BUFFER_SIZE, properties, NioNetwork.class));
            selectorThread = ThreadFactory.getClientSelectorThread();
            writerThread = ThreadFactory.getClientWriterThread();
            writerThread.addNetwork(this);
            return true;
        }
        return false;
    }

    @Override
    public void finishConnect(SelectionKey key) {
        SocketChannel channel = null;
        if (key != null) {
            channel = (SocketChannel) key.channel();
        }
        try {
            if (localPort == 0) {
                localPort = ((InetSocketAddress) channel.getLocalAddress()).getPort();
            }
            Logger.log(LoggerLevel.INFO, "connecting network %s (protocol=%s) %s:%d to %s:%d", name, getProtocol(), localHost, localPort, remoteHost, remotePort);
            if (key != null) {
                while (channel.isConnectionPending()) {
                    channel.finishConnect();
                }
                setChannel(channel);
                this.setConnected(true);
                //key.interestOps(SelectionKey.OP_READ);
            }
            Logger.log(LoggerLevel.INFO, "connected network %s (protocol=%s) %s:%d to %s:%d", name, getProtocol(), localHost, localPort, remoteHost, remotePort);
        } catch (Exception e) {
            ExceptionUtility.handleException(e, "connection network %s (protocol=%s) %s:%d to %s:%d failed", name, getProtocol(), localHost, localPort, remoteHost, remotePort);
            try {
                channel.close();
            } catch (Exception e1) {
            }
            channel = null;
        }
    }

    @Override
    public void finishAccept(SelectionKey key) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = tcpServerChannel.accept();
            SocketAddress socketAddress = socketChannel.getRemoteAddress();

            if (remoteHost.equals(((InetSocketAddress) socketAddress).getAddress().getHostAddress()) && remotePort == ((InetSocketAddress) socketAddress).getPort()) {
                setChannel(socketChannel);
                Logger.log(LoggerLevel.INFO, "connection network %s (protocol=%s) %s:%d from %s:%d is accepted", name, getProtocol(), localHost, localPort, ((InetSocketAddress) socketAddress).getAddress().getHostAddress(), ((InetSocketAddress) socketAddress).getPort());
                return;
            }
            Logger.log(LoggerLevel.ERROR, "connection network %s (protocol=%s) %s:%d from %s:%d is rejected", name, getProtocol(), localHost, localPort, ((InetSocketAddress) socketAddress).getAddress().getHostAddress(), ((InetSocketAddress) socketAddress).getPort());
            socketChannel.close();
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

    public void setChannel(SocketChannel tcpChannel) throws IOException {
        messageBuffer.clear();
        readerBuffer.clear();
        writerBuffer.clear();
        this.tcpChannel = tcpChannel;
        if (sendBuffer > 0) {
            tcpChannel.setOption(StandardSocketOptions.SO_SNDBUF, (int) sendBuffer);
        }
        if (recvBuffer > 0) {
            tcpChannel.setOption(StandardSocketOptions.SO_RCVBUF, (int) recvBuffer);
        }
        if (noDelay) {
            tcpChannel.setOption(StandardSocketOptions.TCP_NODELAY, noDelay);
        }

        tcpChannel.configureBlocking(false);
        tcpChannel.register(selectorThread.getSelector(), SelectionKey.OP_READ, this);
    }

    public void setChannel(SocketChannel tcpChannel, boolean flag) throws IOException {
        messageBuffer.clear();
        readerBuffer.clear();
        writerBuffer.clear();
        this.tcpChannel = tcpChannel;
        if (sendBuffer > 0) {
            tcpChannel.setOption(StandardSocketOptions.SO_SNDBUF, (int) sendBuffer);
        }
        if (recvBuffer > 0) {
            tcpChannel.setOption(StandardSocketOptions.SO_RCVBUF, (int) recvBuffer);
        }
        if (noDelay) {
            tcpChannel.setOption(StandardSocketOptions.TCP_NODELAY, noDelay);
        }

        tcpChannel.configureBlocking(false);
    }

    @Override
    public AbstractSelectableChannel getChannel() {
        return tcpChannel;
    }

    @Override
    public Payload[] readFromChannel() throws IOException {

        int readBytes = tcpChannel.read(readerBuffer);

        if (readBytes == -1) {
            throw new IOException(name + " connection closed");
        }
        if (readBytes == 0) {
            return new Payload[]{};
        }
        byte[] data = new byte[readBytes];
        readerBuffer.flip();
        readerBuffer.get(data);
        messageBuffer.check(data.length);
        messageBuffer.put(data);
        if (readerBuffer.hasRemaining()) {
            readerBuffer.compact();
        } else {
            readerBuffer.clear();
        }
        payloads.clear();

        while (messageBuffer.position() > 0) {
            // because we need at least 8 bytes to check header and version
            if (messageBuffer.position() < 4) {
                break;
            }
            messageBuffer.flip();
            int header = messageBuffer.getInt();
            byte version = (byte) ((header >> 24) & 0xFF);
            // version checks whether we are at start of iam message
            if (version == DiameterMessage.VERSION) {
                int length = header & 0xFFFFFF;
                // if current messagebuffer contains less bytes than required
                // then we wait for next chunk of bytes

                if (messageBuffer.remaining() < (length - 4)) {
                    byte[] remaining = new byte[messageBuffer.remaining()];
                    messageBuffer.get(remaining);
                    messageBuffer.clear();
                    // we re-add header because we again will start with version check -86 thin checl while loop above
                    // thus we add header first, and then remaining bytes and wait for the next chunk
                    messageBuffer.putInt(header);
                    messageBuffer.put(remaining);
                    break;
                } else {
                    byte[] rem = new byte[length - 4];
                    //messageBuffer.flip();
                    messageBuffer.get(rem);
                    messageBuffer.compact();
                    byte[] bytes = ByteBuffer.allocate(length).putInt(header).array();
                    System.arraycopy(rem, 0, bytes, 4, rem.length);
                    Payload payload = new Payload(bytes);
                    payloads.add(payload);
                }
            } else {
                // if somehow bytes are missing, then we loop until we find our message separator IamMessage.VERSION
                while (version != DiameterMessage.VERSION && messageBuffer.position() < messageBuffer.limit()) {
                    version = messageBuffer.get();
                }
                if (version == DiameterMessage.VERSION) {
                    // if find it then we re-add it to bytebuffer and return to the beginning of loop
                    // and start parsing again
                    byte[] remaining = new byte[messageBuffer.remaining()];
                    messageBuffer.get(remaining);
                    messageBuffer.clear();
                    messageBuffer.put(version);
                    messageBuffer.put(remaining);
                } else {
                    // if we have reached end of byte buffer without finding IamMessage.VERSION then we can clear because
                    // there is no chance to parse it
                    messageBuffer.clear();
                }
            }
        }

        Payload[] array = new Payload[payloads.size()];
        payloads.toArray(array);
        return array;
    }

    @Override
    public int writeToChannel(java.nio.ByteBuffer buffer, DiameterMessage message) throws IOException {
        buffer.flip();
        return tcpChannel.write(buffer);
    }

    @Override
    public void startServer() throws IOException {
        long start = System.currentTimeMillis();
        Logger.log(LoggerLevel.INFO, "starting network %s (protocol=%s) as server %s:%d", name, getProtocol(), localHost, localPort);
        try {
            tcpServerChannel = ServerSocketChannel.open();
            tcpServerChannel.configureBlocking(false);
            tcpServerChannel.bind(new InetSocketAddress(localHost, localPort));
            ChangeRequest change = new ChangeRequest(tcpServerChannel, ChangeRequest.REGISTER, SelectionKey.OP_ACCEPT, this);
            selectorThread.handleChange(change);
            Logger.log(LoggerLevel.INFO, "started  network %s in %d msec", name, (System.currentTimeMillis() - start));
        } catch (BindException e) {
            ExceptionUtility.handleException(e, "starting network %s is failed %s:%d because address is already in use", name, localHost, localPort);
            throw e;
        }
    }

    @Override
    public void connect() throws IOException {
        if (this.tcpChannel != null && this.tcpChannel.isConnected()) {
            this.tcpChannel.close();
            this.tcpChannel = null;
        }
        SocketChannel tcpChannel = SocketChannel.open();
        if (sendBuffer > 0) {
            tcpChannel.setOption(StandardSocketOptions.SO_SNDBUF, (int) sendBuffer);
        }
        if (recvBuffer > 0) {
            tcpChannel.setOption(StandardSocketOptions.SO_RCVBUF, (int) recvBuffer);
        }
        if (noDelay) {
            tcpChannel.setOption(StandardSocketOptions.TCP_NODELAY, noDelay);
        }
        tcpChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        tcpChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        try {
            tcpChannel.bind(new InetSocketAddress(localHost, 0));
            tcpChannel.configureBlocking(false);
            tcpChannel.connect(new InetSocketAddress(remoteHost, remotePort));
            this.tcpChannel = tcpChannel;
            selectorThread.handleChange(new ChangeRequest(tcpChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT, this));
        } catch (IOException e) {
            ExceptionUtility.handleException(e, "connection network %s (protocol=%s) %s:%d to %s:%d failed", name, getProtocol(), localHost, localPort, remoteHost, remotePort);
            tcpChannel.close();
            tcpChannel = null;
            throw e;
        }
    }

    @Override
    public void disconnect() {
        try {
            tcpChannel.close();
        } catch (Exception e) {
        }
        tcpChannel = null;
    }

    @Override
    public InetAddress getRemoteAddress() {
        try {
            SocketAddress socketAddress = tcpChannel.getRemoteAddress();
            InetSocketAddress address = (InetSocketAddress) socketAddress;
            return address.getAddress();
        } catch (IOException e) {
            return null;
        }
    }

}
