package com.linkedlogics.diameter.parser;

import com.linkedlogics.diameter.exception.AvpException;
import com.linkedlogics.diameter.exception.ParseException;

import java.io.*;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class AvpTypeParser {

    private static final long SECOND_SHIFT = 2208988800L;

    private static final int INT_INET4 = 1;
    private static final int INT_INET6 = 2;

    private static final int INT32_SIZE = 4;
    private static final int INT64_SIZE = 8;
    private static final int FLOAT32_SIZE = 4;
    private static final int FLOAT64_SIZE = 8;

    protected static ByteBuffer getByteBuffer(byte[] bytes, int length) throws AvpException {
        if (bytes.length != length) throw new AvpException("Incorrect data length");
        return ByteBuffer.wrap(bytes);
    }

    public static int bytesToInt32(byte[] rawData) throws AvpException {
        return getByteBuffer(rawData, INT32_SIZE).getInt();
    }

    public static long bytesToInt64(byte[] rawData) throws AvpException {
        return getByteBuffer(rawData, INT64_SIZE).getLong();
    }

    public static float bytesToFloat32(byte[] rawData) throws AvpException {
        return getByteBuffer(rawData, FLOAT32_SIZE).getFloat();
    }

    public static double bytesToFloat64(byte[] rawData) throws AvpException {
        return getByteBuffer(rawData, FLOAT64_SIZE).getDouble();
    }

    public static String bytesToHex(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(Integer.toString(b, 16));
        }
        return builder.toString();
    }

    public static String bytesToOctetString(byte[] rawData) throws UnsupportedEncodingException {
        try {
            return new String(rawData, "iso-8859-15");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }
    }

    public static String bytesToUtf8String(byte[] rawData) throws UnsupportedEncodingException {
        try {
            return new String(rawData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }
    }

    public static Date bytesToDate(byte[] rawData) throws AvpException {
        try {
            byte[] tmp = new byte[8];
            System.arraycopy(rawData, 0, tmp, 4, 4);
            return new Date((bytesToInt64(tmp) - SECOND_SHIFT) * 1000L);
        } catch (Exception e) {
            throw new AvpException(e);
        }
    }

    public static InetAddress bytesToAddress(byte[] rawData) throws AvpException {
        InetAddress inetAddress;
        byte[] address;
        try {
            if (rawData[1] == INT_INET4) {
                address = new byte[4];
                System.arraycopy(rawData, 2, address, 0, address.length);
                inetAddress = Inet4Address.getByAddress(address);
            } else {
                address = new byte[16];
                System.arraycopy(rawData, 2, address, 0, address.length);
                inetAddress = Inet6Address.getByAddress(address);
            }
        } catch (Exception e) {
            throw new AvpException(e);
        }
        return inetAddress;
    }

    public static byte[] int32ToBytes(int value) {
        byte[] bytes = new byte[INT32_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(value);
        return bytes;
    }

    public static byte[] unsignedInt32ToBytes(long value) {
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(value);
        buffer.flip();
        buffer.get(bytes);
        buffer.get(bytes);
        return bytes;
    }

    public static byte[] int64ToBytes(long value) {
        byte[] bytes = new byte[INT64_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putLong(value);
        return bytes;
    }

    public static byte[] float32ToBytes(float value) {
        byte[] bytes = new byte[FLOAT32_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putFloat(value);
        return bytes;
    }

    public static byte[] float64ToBytes(double value) {
        byte[] bytes = new byte[FLOAT64_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putDouble(value);
        return bytes;
    }

    public static byte[] octetStringToBytes(String value) throws ParseException {
        try {
            return value.getBytes("iso-8859-15");
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    public static byte[] utf8StringToBytes(String value) throws ParseException {
        try {
            return value.getBytes("utf8");
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    public static byte[] addressToBytes(InetAddress address) {
        byte addressBytes[] = address.getAddress();
        byte[] data = new byte[addressBytes.length + 2];
        int addressType = address instanceof Inet4Address ? INT_INET4 : INT_INET6;
        data[0] = (byte) ((addressType >> 8) & 0xFF);
        data[1] = (byte) (addressType & 0xFF);
        System.arraycopy(addressBytes, 0, data, 2, addressBytes.length);
        return data;
    }

    public static byte[] dateToBytes(Date date) {
        byte[] data = new byte[4];
        System.arraycopy(int64ToBytes((date.getTime() / 1000L) + SECOND_SHIFT), 4, data, 0, 4);
        return data;
    }
}

