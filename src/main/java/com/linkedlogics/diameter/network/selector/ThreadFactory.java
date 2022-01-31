package com.linkedlogics.diameter.network.selector;

import java.nio.channels.SelectableChannel;
import java.nio.channels.spi.AbstractSelectableChannel;

public class ThreadFactory {

    private static SelectorThread serverSelectorThread = new SelectorThread();
    private static WriterThread serverWriterThread = new WriterThread();
    private static SelectorThread clientSelectorThread = new SelectorThread();
    private static WriterThread clientWriterThread = new WriterThread();

    static {
        serverSelectorThread.start();
        serverWriterThread.start();
        clientSelectorThread.start();
        clientWriterThread.start();
    }

    public static SelectorThread getServerSelectorThread() {
        return serverSelectorThread;
    }

    public static WriterThread getServerWriterThread() {
        return serverWriterThread;
    }

    public static SelectorThread getClientSelectorThread() {
        return clientSelectorThread;
    }

    public static WriterThread getClientWriterThread() {
        return clientWriterThread;
    }


}
