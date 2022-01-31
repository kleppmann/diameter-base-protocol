package com.linkedlogics.diameter;

import com.linkedlogics.application.logger.LoggerApplication;
import com.linkedlogics.diameter.exception.AvpException;
import com.linkedlogics.diameter.network.TcpServerNetwork;
import org.dom4j.*;

import java.io.*;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class Run {

    public static void main(String args[]) throws UnsupportedEncodingException, AvpException, DocumentException {


        try {

            new LoggerApplication().start(null);
            TcpServerNetwork server = new TcpServerNetwork();
            server.init();
            server.startServer();


        } catch (Exception e) {
            e.printStackTrace();
        }

//        AvpManager manager = AvpManager.getInstance();
//        AvpDefinition avpDefinition = manager.getAvp(296, 0);
//        System.out.println(avpDefinition);


        /*
        SAXReader reader = new SAXReader();
        Document document = reader.read("C:\\dictionary.xml");
        Element root = document.getRootElement();
        List<Element> elements = root.elements("avpdefn");
        Set<String> set = new HashSet<>();

        for (Element element : elements) {
            Integer code = Integer.valueOf(element.attribute("code").getValue());
            String vendorId = "0";
            if (element.attribute("vendor-id") != null) vendorId = element.attribute("vendor-id").getValue();
            String key = code + "_" + vendorId;
            if (set.contains(key)) System.out.println(element.attribute("name") +" "+key);
            set.add(key);
        }

        System.out.println(set.size());
        */
    }
}

