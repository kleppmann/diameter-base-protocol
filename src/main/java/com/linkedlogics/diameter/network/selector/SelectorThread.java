package com.linkedlogics.diameter.network.selector;

import com.linkedlogics.application.exception.ExceptionUtility;
import com.linkedlogics.application.property.Property;
import com.linkedlogics.diameter.network.NioNetwork;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SelectorThread extends Thread {
	protected Selector selector ;
	protected Queue<ChangeRequest> changeRequests = new ConcurrentLinkedQueue<>();
	protected boolean isRunning ;
	protected boolean isStopping ;
	protected boolean isShared ;
	protected long delay ;
	
	public SelectorThread() {
		try {
			selector = SelectorProvider.provider().openSelector() ;
		} catch (IOException e) {
			throw new RuntimeException(e) ;
		}
	}
	
	public Selector getSelector() {
		return selector;
	}
	
	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

	public boolean handleChange(final ChangeRequest change) {
		synchronized (changeRequests) {
			return changeRequests.offer(change) ;
		}
	}
	
	public void run() {
		isRunning = true ;
		isStopping = false ;
	
		while (isRunning) {
			try {
				synchronized (changeRequests) {
					Iterator<ChangeRequest> changes = changeRequests.iterator() ;
					while (changes.hasNext()) {
						ChangeRequest change = changes.next() ;
						changes.remove() ;
						if (change == null) {
							continue ;
						}
						NioNetwork network ;
						switch (change.getType()) {
						case ChangeRequest.CONNECT:
							network = ((NioNetwork) change.getAttachment());
							network.connect() ; 
							break ;
						case ChangeRequest.SHUTDOWN:
							network = ((NioNetwork) change.getAttachment());
							network.disconnect() ;
							break ;
						case ChangeRequest.REGISTER:
							change.getSocket().register(selector, change.getOps(), change.getAttachment()) ;
							break ;
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.getSocket().keyFor(this.selector);
				            key.interestOps(change.getOps());
							break ;
						}
					}
				}
				
				this.selector.select(5) ;
				
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator() ;
				while (keys.hasNext()) {
					SelectionKey key = keys.next() ;
					keys.remove() ;
					if (!key.isValid()) {
						continue ;
					}
					if (key.isConnectable()) {
						connect(key) ;
					} else if (key.isAcceptable()) {
						accept(key) ;
					} else if (key.isReadable()) {
						read(key) ;
					}
				}
			} catch (Throwable e) {
				ExceptionUtility.handleException(e) ;
			}
		}
		try {
			selector.close() ;
		} catch (IOException e) { }
		
		isStopping = false ;
	}
	
	public void shutdown() {
		if (isRunning) {
			isRunning = false ;
			isStopping = true ;
			while (isStopping) {
				try {
					Thread.sleep(10) ;
				} catch (InterruptedException e) { }
			}
		}
	}
	
	public void connect(SelectionKey key) {
		NioNetwork network = (NioNetwork) key.attachment() ;
		network.finishConnect(key) ;
	}
	
	public void accept(SelectionKey key) {
		NioNetwork network = (NioNetwork) key.attachment() ;
		network.finishAccept(key) ;
	}
	
	public void read(SelectionKey key) {
		NioNetwork network = (NioNetwork) key.attachment() ;
		network.read(key) ;
	}
	
	@Property(type="Integer", value="1")
	public static final String SELECTORS = "selectors" ;
	@Property(type="Long", value="5")
	public static final String SELECTOR_DELAY = "selector_delay" ;
}
