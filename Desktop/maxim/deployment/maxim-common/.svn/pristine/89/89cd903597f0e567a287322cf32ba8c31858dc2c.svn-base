package com.maxim.util;

import java.io.InputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class JAXUtil {

    public static Object parse(Class<?> type, String xml) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(type);

        XMLInputFactory xif = XMLInputFactory.newInstance();
        
        // DO NOT support, then prevent XML injection 
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xml));

        return context.createUnmarshaller().unmarshal(xsr);
    }
    
    public static Object parse(Class<?> type, InputStream input) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(type);

        XMLInputFactory xif = XMLInputFactory.newInstance();
        
        // DO NOT support, then prevent XML injection 
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader xsr = xif.createXMLStreamReader(input);

        return context.createUnmarshaller().unmarshal(xsr);
    }

}
