package com.linkedlogics.diameter.data.file;

import com.linkedlogics.diameter.object.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.*;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class AvpAdapter implements com.linkedlogics.diameter.data.AvpAdapter {

    private static AvpAdapter instance;
    private Map<String, AvpDefinition> avpCodeMap;
    private Map<String, AvpDefinition> avpNameMap;
    private Map<String, MessageDefinition> messageMap;

    private AvpAdapter() {
        avpCodeMap = new HashMap<>();
        avpNameMap = new HashMap<>();
        messageMap = new HashMap<>();
    }

    public static AvpAdapter getInstance() {
        if (instance == null)
            instance = new AvpAdapter();
        return instance;
    }

    public void init() {
        loadAvps();
        loadMessages();
    }

    private Document getDocument(String file) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(file);
    }

    @SuppressWarnings("unchecked")
    public void loadMessages() {
        try {
            Document document = getDocument("C:\\dictionary.xml");
            List<Element> apps = document.getRootElement().elements("application");

            for (Element app : apps) {
                List<Element> messages = app.elements("command");
                for (Element message : messages) {
                    MessageDefinition messageDefinition = new MessageDefinition();
                    messageDefinition.setName(message.attribute("name").getValue());
                    messageDefinition.setCode(Integer.valueOf(message.attribute("code").getValue()));
                    messageDefinition.setRequest(Boolean.valueOf(message.attribute("request").getValue()));
                    List<Element> avps = message.elements("avp");
                    List<AvpDefinition> avpList = new ArrayList<>();
                    for (Element avp : avps) {
                        avpList.add(avpNameMap.get(avp.attribute("name").getValue()));
                    }
                    messageDefinition.setAvps(avpList);
                    String key = messageDefinition.getCode() + "_" + (messageDefinition.isRequest() ? 1 : 0);
                    messageMap.put(key, messageDefinition);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAvps() {
        try {

            Document document = getDocument("C:\\dictionary.xml");
            Element root = document.getRootElement();
            List<Element> elements = root.elements("avpdefn");
            int count = 0, total = elements.size();

            while (count < total) {

                Iterator<Element> it = elements.iterator();

                outerloop:
                while (it.hasNext()) {
                    Element element = it.next();
                    AvpDefinition avpDefinition = new AvpDefinition();
                    avpDefinition.setName(element.attribute("name").getValue());
                    avpDefinition.setCode(Integer.valueOf(element.attribute("code").getValue()));
                    if (element.attribute("vendor-id") == null) {
                        avpDefinition.setVendorId(0);
                    } else {
                        VendorType vendorType = VendorType.getByName(element.attribute("vendor-id").getValue());
                        if (vendorType != null)
                            avpDefinition.setVendorId(vendorType.getCode());
                    }
                    if (element.element("type") != null) {
                        AvpType type = AvpType.getByName(element.element("type").attribute("type-name").getValue());
                        avpDefinition.setAvpType(type);

                        if (type == AvpType.Enumerated) {
                            List<Element> elementList = element.element("type").elements("enum");
                            List<EnumDefinition> enums = new ArrayList<>();
                            for (Element e : elementList) {
                                enums.add(new EnumDefinition(Long.valueOf(e.attribute("code").getValue()), e.attribute("name").getValue()));
                            }
                            avpDefinition.setEnums(enums);
                        }

                        avpCodeMap.put(avpDefinition.getCode() + "_" + avpDefinition.getVendorId(), avpDefinition);
                        avpNameMap.put(avpDefinition.getName(), avpDefinition);
                        it.remove();
                        count++;
                    } else {
                        avpDefinition.setAvpType(AvpType.Grouped);
                        Element grouped = element.element("grouped");
                        List<Element> avps = grouped.elements("avp");
                        List<AvpDefinition> list = new ArrayList<>();
                        for (Element avp : avps) {
                            String name = avp.attribute("name").getValue();
                            AvpDefinition definition = avpNameMap.get(name);
                            if (definition == null) continue outerloop;
                            list.add(definition);
                        }
                        avpDefinition.setAvps(list);
                        avpCodeMap.put(avpDefinition.getCode() + "_" + avpDefinition.getVendorId(), avpDefinition);
                        avpNameMap.put(avpDefinition.getName(), avpDefinition);
                        it.remove();
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AvpDefinition getAvp(int code) {
        return getAvp(code, 0);
    }

    @Override
    public AvpDefinition getAvp(int code, long vendorId) {
        String key = code + "_" + vendorId;
        return avpCodeMap.get(key);
    }

    @Override
    public MessageDefinition getMessage(int code, int isRequest) {
        return messageMap.get(code + "_" + isRequest);
    }

    public void show() {
        System.out.println(avpCodeMap.size() + " " + avpNameMap.size() + " " + messageMap.size());
    }
}
