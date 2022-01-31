package com.linkedlogics.diameter.parser;

import com.linkedlogics.application.logger.Logger;
import com.linkedlogics.application.logger.LoggerLevel;
import com.linkedlogics.diameter.exception.AvpException;
import com.linkedlogics.diameter.exception.ParseException;
import com.linkedlogics.diameter.object.Avp;
import com.linkedlogics.diameter.object.AvpCode;
import com.linkedlogics.diameter.object.AvpList;
import com.linkedlogics.diameter.object.DiameterMessage;
import com.linkedlogics.diameter.utility.UIDGenerator;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class MessageParser extends AvpParser {


    public final static UIDGenerator endToEndGen = new UIDGenerator(
            (int) (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) & 0xFFF) << 20
    );

    public static DiameterMessage decodeDiameterMessage(byte[] buffer) throws AvpException {

        try {

            if (buffer == null) throw new AvpException("Buffer is null");
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer, 0, buffer.length));

            if (buffer.length < 20) throw new AvpException("Not enough byte in buffer");
            int tmp = in.readInt();

            int version = (tmp >> 24) & 0xFF;
            if (version != 1) throw new AvpException("Incorrect version of diameter message");

            int length = tmp & 0xFFFFFF;
            if (length != buffer.length) throw new AvpException("Incorrect length of data");

            tmp = in.readInt();
            int flags = (tmp >> 24) & 0xFF;
            int code = tmp & 0xFFFFFF;
            long applicationId = ((long) in.readInt() << 32) >>> 32;
            long hopByHopIdentifier = ((long) in.readInt() << 32) >>> 32;
            long endToEndIdentifier = ((long) in.readInt() << 32) >>> 32;

            if (length < 0) throw new AvpException("Avp length is negative");

            AvpList avps = decodeAvpGrouped(buffer, 20);
            return new DiameterMessage(code, (short) flags, applicationId, hopByHopIdentifier, endToEndIdentifier, avps);

        } catch (Exception e) {
            throw new AvpException(e);
        }
    }

    public static byte[] encodeMessage(DiameterMessage message) throws ParseException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] rawData = encodeAvpGrouped(message.getAvpList());
            DataOutputStream data = new DataOutputStream(out);
            int tmp = (1 << 24) & 0xFF000000;
            tmp += 20 + rawData.length;
            data.writeInt(tmp);
            tmp = (message.getFlags() << 24) & 0xFF000000;
            tmp += message.getCode();
            data.writeInt(tmp);
            data.write(toBytes(message.getApplicationId()));
            data.write(toBytes(message.getHopByHopIdentifier()));
            data.write(toBytes(message.getEndToEndIdentifier()));
            data.write(rawData);
            return out.toByteArray();

        } catch (Exception e) {
            throw new ParseException("Failed to encode message.", e);
        }
    }

    private static byte[] toBytes(long value) {
        byte[] data = new byte[4];
        data[0] = (byte) ((value >> 24) & 0xFF);
        data[1] = (byte) ((value >> 16) & 0xFF);
        data[2] = (byte) ((value >> 8) & 0xFF);
        data[3] = (byte) ((value) & 0xFF);
        return data;
    }

    public static DiameterMessage createMessage(DiameterMessage message) {
        return createMessage(message, message.getCode());
    }

    public static DiameterMessage createMessage(DiameterMessage message, int commandCode) {

        DiameterMessage newMessage = new DiameterMessage(
                commandCode,
                (short) message.getFlags(),
                message.getApplicationId(),
                message.getHopByHopIdentifier(),
                endToEndGen.nextLong(),
                null
        );
        copyBasicAvps(newMessage, message, false);

        return newMessage;
    }

    public static void copyBasicAvps(DiameterMessage newMessage, DiameterMessage message, boolean invertPoints) {
        //left it here, but
        Avp avp;
        // Copy session id's information
        {
            avp = message.getAvpList().getAvp(AvpCode.SESSION_ID);
            if (avp != null) {
                newMessage.getAvpList().addAvp(new Avp(avp));
            }
            avp = message.getAvpList().getAvp(AvpCode.ACC_SESSION_ID);
            if (avp != null) {
                newMessage.getAvpList().addAvp(new Avp(avp));
            }
            avp = message.getAvpList().getAvp(AvpCode.ACC_SUB_SESSION_ID);
            if (avp != null) {
                newMessage.getAvpList().addAvp(new Avp(avp));
            }
            avp = message.getAvpList().getAvp(AvpCode.ACC_MULTI_SESSION_ID);
            if (avp != null) {
                newMessage.getAvpList().addAvp(new Avp(avp));
            }
        }
        // Copy Applicatio id's information
        {
            avp = message.getAvpList().getAvp(AvpCode.VENDOR_SPECIFIC_APPLICATION_ID);
            if (avp != null) {
                newMessage.getAvpList().addAvp(new Avp(avp));
            }
            avp = message.getAvpList().getAvp(AvpCode.ACCT_APPLICATION_ID);
            if (avp != null) {
                newMessage.getAvpList().addAvp(new Avp(avp));
            }
            avp = message.getAvpList().getAvp(AvpCode.AUTH_APPLICATION_ID);
            if (avp != null) {
                newMessage.getAvpList().addAvp(new Avp(avp));
            }
        }
        // Copy proxy information
        {
            avp = message.getAvpList().getAvp(AvpCode.PROXY_INFO);
            if (avp != null) {
                AvpList avps;
                try {
                    avps = avp.getAvpGrouped();
                    for (Avp avpp : avps) {
                        newMessage.getAvpList().addAvp(new Avp(avpp));
                    }
                } catch (AvpException e) {
                    Logger.log(LoggerLevel.DEBUG, e, "Error copying Proxy-Info AVP");
                }
            }
        }
        // Copy route information
        {
            if (newMessage.isRequest()) {
                if (invertPoints) {
                    // set Dest host
                    avp = message.getAvpList().getAvp(AvpCode.ORIGIN_HOST);
                    if (avp != null) {
                        newMessage.getAvpList().addAvp(new Avp(AvpCode.DESTINATION_HOST, avp));
                    }
                    // set Dest realm
                    avp = message.getAvpList().getAvp(AvpCode.ORIGIN_REALM);
                    if (avp != null) {
                        newMessage.getAvpList().addAvp(new Avp(AvpCode.DESTINATION_REALM, avp));
                    }
                } else {
                    // set Dest host
                    avp = message.getAvpList().getAvp(AvpCode.DESTINATION_HOST);
                    if (avp != null) {
                        newMessage.getAvpList().addAvp(avp);
                    }
                    // set Dest realm
                    avp = message.getAvpList().getAvp(AvpCode.DESTINATION_REALM);
                    if (avp != null) {
                        newMessage.getAvpList().addAvp(avp);
                    }
                }
            }
        }
    }

    public static DiameterMessage createMessage(int code, long headerAppId) {
        return new DiameterMessage(code, headerAppId);
    }

}

