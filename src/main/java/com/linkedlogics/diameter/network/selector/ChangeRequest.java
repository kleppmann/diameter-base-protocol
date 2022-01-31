package com.linkedlogics.diameter.network.selector;

import java.nio.channels.SelectableChannel;
import java.nio.channels.spi.AbstractSelectableChannel;

public class ChangeRequest {
	public static final int REGISTER = 1;
	public static final int CHANGEOPS = 2;
	public static final int CONNECT = 3 ;
	public static final int SHUTDOWN = 4 ;

	private SelectableChannel socket;
	private int type;
	private int ops;
	private Object attachment ;
	private long delay ;

	public ChangeRequest(AbstractSelectableChannel socket, int type, int ops, Object attachment) {
		this.socket = socket;
		this.type = type;
		this.ops = ops;
		this.attachment = attachment ;
	}
	
	public ChangeRequest(AbstractSelectableChannel socket, int type, Object attachment) {
		this.socket = socket;
		this.type = type;
		this.attachment = attachment ;
	}

	public SelectableChannel getSocket() {
		return socket;
	}

	public int getType() {
		return type;
	}

	public int getOps() {
		return ops;
	}

	public Object getAttachment() {
		return attachment;
	}

	public long getDelay() {
		return delay;
	}

	public ChangeRequest setDelay(long delay) {
		this.delay = delay;
		return this ;
	}
}
