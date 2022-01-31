package com.linkedlogics.diameter.network;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IamByteBuffer {
	private java.nio.ByteBuffer buffer ;
	private boolean isDirect ;
	
	public IamByteBuffer(int capacity, boolean isDirect) {
		this.isDirect = isDirect ;
		if (isDirect) 
			buffer = ByteBuffer.allocateDirect(capacity) ;
		else
			buffer = ByteBuffer.allocate(capacity) ;
	}
	
	public IamByteBuffer(int capacity) {
		this(capacity, false) ;
	}

	public ByteBuffer check(int length) {
		if (buffer.position() + length > buffer.capacity()) {
			int capacity = buffer.capacity() ;
			byte[] messageData = new byte[buffer.position()] ;
			buffer.flip() ;
			buffer.get(messageData) ;

			if (isDirect) 
				buffer = ByteBuffer.allocateDirect(capacity + length * 2) ;
			else
				buffer = ByteBuffer.allocate(capacity + length * 2) ;
			
			buffer.put(messageData) ;
		}
		return buffer ;
	}

	public void clear() {
		buffer.clear() ;
	}
	
	public void putLong(long value) {
		buffer.putLong(value) ;
	}
	
	public void putLong(int index, long value) {
		buffer.putLong(index, value) ;
	}
	
	public long getLong() {
		return buffer.getLong() ;
	}
	
	public long getLong(int index) {
		return buffer.getLong(index) ;
	}
	
	public void putInt(int value) {
		buffer.putInt(value) ;
	}
	
	public void putInt(int index, int value) {
		buffer.putInt(index, value) ;
	}
	
	public int getInt() {
		return buffer.getInt() ;
	}
	
	public int getInt(int index) {
		return buffer.getInt(index) ;
	}
	
	public void putShort(short value) {
		buffer.putShort(value) ;
	}
	
	public void putShort(int index, short value) {
		buffer.putShort(index, value) ;
	}
	
	public short getShort() {
		return buffer.getShort() ;
	}
	
	public short getShort(int index) {
		return buffer.getShort(index) ;
	}
	
	public void put(byte[] value) {
		buffer.put(value) ;
	}
	
	public void put(byte value) {
		buffer.put(value) ;
	}
	
	public void put(int index, byte value) {
		buffer.put(index, value) ;
	}
	
	public void put(byte[] value, int offset, int length) {
		buffer.put(value, offset, length) ;
	}
	
	public void putChar(char value) {
		buffer.putChar(value) ;
	}
	
	public void putChar(int index, char value) {
		buffer.putChar(index, value) ;
	}
	
	public void putDouble(double value) {
		buffer.putDouble(value) ;
	}
	
	public void putDouble(int index, double value) {
		buffer.putDouble(index, value) ;
	}
	
	public void putFloat(float value) {
		buffer.putFloat(value) ;
	}
	
	public void putFloat(int index, float value) {
		buffer.putFloat(index, value) ;
	}
	
	public byte get() {
		return buffer.get() ;
	}
	
	public byte get(int index) {
		return buffer.get(index) ;
	}
	
	public void get(byte[] value) {
		buffer.get(value) ;
	}
	
	public void get(byte[] value, int offset, int length) {
		buffer.get(value, offset, length) ;
	}
	
	public char getChar() {
		return buffer.getChar() ;
	}
	
	public char getChar(int index) {
		return buffer.getChar(index) ;
	}
	
	public double getDouble() {
		return buffer.getDouble() ;
	}
	
	public double getDouble(int index) {
		return buffer.getDouble(index) ;
	}
	
	public double getFloat() {
		return buffer.getFloat() ;
	}
	
	public double getFloat(int index) {
		return buffer.getFloat(index) ;
	}
	
	public void flip() {
		buffer.flip() ;
	}
	
	public int remaining() {
		return buffer.remaining() ;
	}
	
	public int position() {
		return buffer.position() ;
	}
	
	public boolean hasArray() {
		return buffer.hasArray() ;
	}
	
	public int hashCode() {
		return buffer.hashCode() ;
	}
	
	public boolean isDirect() {
		return buffer.isDirect() ;
	}
	
	public ByteOrder order() {
		return buffer.order() ;
	}
	
	public byte[] array() {
		return buffer.array() ;
	}
	
	public int arrayOffset() {
		return buffer.arrayOffset() ;
	}
	
	public int capacity() {
		return buffer.capacity() ;
	}
	
	public boolean hasRemaining() {
		return buffer.hasRemaining() ;
	}
	
	public boolean isReadOnly() {
		return buffer.isReadOnly() ;
	}
	
	public int limit() {
		return buffer.limit() ;
	}
	
	public void reset() {
		buffer.reset() ;
	}
	
	public void rewind() {
		buffer.rewind() ;
	}
	
	public void compact() {
		buffer.compact() ;
	}
	
}
