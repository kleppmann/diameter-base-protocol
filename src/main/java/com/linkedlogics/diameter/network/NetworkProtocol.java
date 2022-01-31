package com.linkedlogics.diameter.network;

public enum NetworkProtocol {
	unknown(0), 
	tcp(0x01), 
	udp(0x02), 
	sctp(0x04), 
	smpp(0x08), 
	http(0x10), 
	diameter(0x20), 
	ftp(0x40), 
	snmp(0x80),
	smtp(0x100),
	loopback(0x200);
	
	private int value;

	private NetworkProtocol(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}
}
