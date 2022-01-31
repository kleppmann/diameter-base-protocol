package com.linkedlogics.diameter.parser;

import com.linkedlogics.diameter.exception.AvpException;
import com.linkedlogics.diameter.object.Avp;
import com.linkedlogics.diameter.object.AvpList;
import com.linkedlogics.diameter.object.AvpType;

import java.io.*;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class AvpParser {

    public static AvpList decodeAvpGrouped(byte[] buffer, int shift) throws AvpException, IOException {
        return decodeAvpGrouped(new AvpList(), buffer, shift);
    }

    public static AvpList decodeAvpGrouped(byte[] buffer) throws AvpException, IOException {
        return decodeAvpGrouped(new AvpList(), buffer, 0);
    }

    public static AvpList decodeAvpGrouped(AvpList avpGrouped, byte[] buffer, int shift) throws AvpException, IOException {

        try {

            if (buffer == null) throw new AvpException("Buffer is null");
            if (shift >= buffer.length) throw new AvpException("Shift is greater than buffer size");
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer, shift, buffer.length));
            int pos = shift;

            while (pos < buffer.length) {

                if (pos + 8 > buffer.length) throw new AvpException("Not enough byte in buffer");
                int code = in.readInt();
                int tmp = in.readInt();
                int flags = (tmp >> 24) & 0xFF;
                int length = tmp & 0xFFFFFF;

                if (length < 0) throw new AvpException("Avp length is negative");
                if (length + pos > buffer.length) throw new AvpException("Not enough byte in buffer");

                int vendorId = 0;
                if ((flags & 0x80) != 0)
                    vendorId = in.readInt();

                byte[] rawData = new byte[length - (8 + (vendorId == 0 ? 0 : 4))];
                in.read(rawData);

                if (length % 4 != 0) {
                    for (int i; length % 4 != 0; length += i) {
                        i = (int) in.skip((4 - length % 4));
                    }
                }

                Avp avp = new Avp(code, flags, vendorId, rawData);
                pos += length;
                avpGrouped.addAvp(avp);
                if (avp.getAvpType() == AvpType.Grouped)
                    avp.setAvpGrouped(decodeAvpGrouped(new AvpList(), avp.getRawData(), 0));
            }

            return avpGrouped;
        } catch (Exception e) {
            throw new AvpException(e);
        }
    }

    public static byte[] encodeAvp(Avp avp) throws AvpException, IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (avp == null) return null;
            DataOutputStream data = new DataOutputStream(out);
            data.writeInt(avp.getCode());
            int flags = avp.getFlags();
            int length = avp.getLength();
            data.writeInt(((flags << 24) & 0xFF000000) + length);

            if (avp.isVendorId())
                data.writeInt((int) avp.getVendorId());
            out.write(avp.getRawData());

            if (avp.getRawData().length % 4 != 0) {
                for (int i = 0; i < 4 - avp.getRawData().length % 4; i++) {
                    out.write(0);
                }
            }
            return out.toByteArray();

        } catch (Exception e) {
            throw new AvpException(e);
        }
    }

    public static byte[] encodeAvpGrouped(AvpList avpGrouped) throws AvpException, IOException {
        return encodeAvpGrouped(avpGrouped, new ByteArrayOutputStream());
    }

    public static byte[] encodeAvpGrouped(AvpList avpGrouped, ByteArrayOutputStream out) throws AvpException, IOException {
        try {
            DataOutputStream data = new DataOutputStream(out);
            for (Avp avp : avpGrouped) {
                byte[] encoded = encodeAvp(avp);
                if (encoded != null) data.write(encoded);
            }
        } catch (Exception e) {
            throw new AvpException(e);
        }
        return out.toByteArray();
    }

}

