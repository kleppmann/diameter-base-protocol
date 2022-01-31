package com.linkedlogics.diameter.network.selector;

import com.linkedlogics.application.exception.ExceptionUtility;
import com.linkedlogics.application.property.Property;
import com.linkedlogics.diameter.network.NioNetwork;

import java.io.IOException;
import java.util.ArrayList;

public class WriterThread extends Thread {
	private ArrayList<NioNetwork> networkList ;
	private boolean isRunning ;
	private long delay ;
	
	public WriterThread() {
		networkList = new ArrayList<NioNetwork>() ;
	}
	
	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public ArrayList<NioNetwork> getNetworkList() {
		return networkList;
	}

	public synchronized void addNetwork(NioNetwork network) {
		ArrayList<NioNetwork> newList = new ArrayList<>() ;
		for (int i = 0; i < networkList.size(); i++) {
			if (networkList.get(i) == network) {
				return ;
			}
			newList.add(networkList.get(i)) ;
		}
		newList.add(network) ;
		this.networkList = newList ;
	}
	
	public synchronized void removeNetwork(NioNetwork network) {
		ArrayList<NioNetwork> newList = new ArrayList<>() ;
		for (int i = 0; i < networkList.size(); i++) {
			if (networkList.get(i) != network) {
				newList.add(networkList.get(i)) ;
			}
		}
		this.networkList = newList ;
	}
	
	public void run() {
		isRunning = true ;
		while (isRunning) {
			int written = 0 ;
			for (int i = 0; i < networkList.size(); i++) {
				if (networkList.get(i).isConnected()) {
					try {
						written += networkList.get(i).write();
					} catch (IOException e) {
						ExceptionUtility.handleException(e) ;
						//networkList.get(i).getNetworkStateManager().handleEvent(new ConnectionFailed());
					} catch (Throwable e) {
						ExceptionUtility.handleException(e) ;
					}
				}
			}
			
			if (written == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) { }
			}
		}
	}
	
	public void shutdown() {
		isRunning = false ;
	}
	
	@Property(type="Integer", value="1")
	public static final String WRITERS = "writers" ;
	@Property(type="Long", value="1")
	public static final String WRITER_DELAY = "writer_delay" ;
}
