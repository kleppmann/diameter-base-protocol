package com.linkedlogics.diameter;

import com.linkedlogics.application.logger.LoggerApplication;
import com.linkedlogics.diameter.action.Peer;
import com.linkedlogics.diameter.network.TcpNetwork;
import com.linkedlogics.diameter.object.Avp;
import com.linkedlogics.diameter.object.AvpCode;
import com.linkedlogics.diameter.object.DiameterMessage;
import com.linkedlogics.diameter.object.MessageCode;
import com.linkedlogics.diameter.parser.AvpTypeParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class Test {

    public static void main(String args[]) {

        new LoggerApplication().start(null);

        try {
            Peer peer = new Peer();
            peer.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }

//		try {
//
//			byte[] bytes = Files.readAllBytes(Paths.get("C:\\nar.bin"));
//			DiameterMessage message = MessageParser.decodeDiameterMessage(bytes);
//			//System.out.println(message);
//			bytes = MessageParser.encodeMessage(message);
//			System.out.println(bytes.length);
//			message = MessageParser.decodeDiameterMessage(bytes);
//			System.out.println(message);
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}

    }

}
