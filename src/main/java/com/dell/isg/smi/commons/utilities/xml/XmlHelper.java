/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
/**
 * 
 */
package com.dell.isg.smi.commons.utilities.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * The Class XmlHelper.
 */
public class XmlHelper {

    private static final Logger logger = LoggerFactory.getLogger(XmlHelper.class);

    /**
     * Gets the xml from object.
     *
     * @param target the target
     * @return String that represent the object
     */
    public static String GetXmlFromObject(Object target) {
        String result = null;

        try {
            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(target.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(target, writer);
            result = writer.toString();
        } catch (Exception e) {
            logger.error("failed in GetXmlFromObject", e);
        }

        return result;
    }



    /**
     * Xml to object.
     *
     * @param xmlString the xml string
     * @param validate the validate
     * @param cls the cls
     * @return the object
     * @throws JAXBException the JAXB exception
     */
    public static Object xmlToObject(String xmlString, boolean validate, Class cls) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(cls);
        Unmarshaller u = jc.createUnmarshaller();
        return u.unmarshal(new StreamSource(new StringReader(xmlString)));
    }


    /**
     * Xml to object.
     *
     * @param node the node
     * @param cls the cls
     * @return the object
     * @throws JAXBException the JAXB exception
     */
    public static Object xmlToObject(Node node, Class cls) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement root = unmarshaller.unmarshal(node, cls);
        return root.getValue();
    }


    /**
     * Object to complex xml type string.
     *
     * @param obj the obj
     * @param rootName the root name
     * @return the string
     * @throws JAXBException the JAXB exception
     */
    public static String objectToComplexXmlTypeString(Object obj, String rootName) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(new JAXBElement(new QName("", rootName), obj.getClass(), obj), sw);
        return sw.toString();
    }


    /**
     * Complex type XML str to object.
     *
     * @param cls the cls
     * @param xml the xml
     * @return the object
     * @throws JAXBException the JAXB exception
     */
    public static Object complexTypeXMLStrToObject(Class cls, String xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement biosAttrDetails = unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), cls);
        return biosAttrDetails.getValue();
    }


    /**
     * Gets the xml from object.
     *
     * @param target the target
     * @param nameSpaceMap the name space map
     * @return String that represent the object
     */
    public static String GetXmlFromObject(Object target, HashMap<String, String> nameSpaceMap) {
        String result = null;

        try {
            StringWriter writer = new StringWriter();
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            Iterator<Entry<String, String>> it = nameSpaceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                xmlStreamWriter.setPrefix(entry.getKey(), entry.getValue());
            }

            JAXBContext context = JAXBContext.newInstance(target.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(target, xmlStreamWriter);
            result = FixNameSpace(nameSpaceMap, writer.toString());
        } catch (Exception e) {
            logger.error("failed in GetXmlFromObject", e);
        }

        return result;
    }


    /**
     * Fix name space.
     *
     * @param nameSpaceMap the name space map
     * @param target the target
     * @return String that represent the object
     */
    private static String FixNameSpace(HashMap<String, String> nameSpaceMap, String target) {
        StringBuilder nameSpaceContents = new StringBuilder();
        Iterator<Entry<String, String>> it = nameSpaceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            nameSpaceContents.append("xmlns:" + entry.getKey() + "=\"" + entry.getValue() + "\" ");
        }
        return target.replaceAll("xmlns=\"\"", nameSpaceContents.toString());
    }


    /**
     * Gets the object from xml.
     *
     * @param target the target
     * @param xml the xml
     * @return Object
     */
    public static Object GetObjectFromXml(Object target, String xml) {
        Object xmlObject = null;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
            JAXBContext context = JAXBContext.newInstance(target.getClass());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(null);
            xmlObject = target.getClass().cast(unmarshaller.unmarshal(stream));
        } catch (JAXBException e) {
            logger.error("failed in GetXmlFromObject", e);
        }
        return xmlObject;
    }


    /**
     * Convert string to XML document.
     *
     * @param xmlSource the xml source
     * @return the document
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Document convertStringToXMLDocument(String xmlSource) throws ParserConfigurationException, SAXException, IOException {
        return convertStringToXMLDocument(xmlSource, false);
    }


    /**
     * Convert string to XML document.
     *
     * @param xmlSource the xml source
     * @param namespaceAware the namespace aware
     * @return the document
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Document convertStringToXMLDocument(String xmlSource, boolean namespaceAware) throws ParserConfigurationException, SAXException, IOException {
        InputStream inputStream = new ByteArrayInputStream(xmlSource.getBytes());
        return convertInputStreamToXmlDocument(inputStream, namespaceAware);
    }


    /**
     * Convert bytes to xml document.
     *
     * @param inputBytes the input bytes
     * @return the document
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Document convertBytesToXmlDocument(byte[] inputBytes) throws ParserConfigurationException, SAXException, IOException {
        return convertBytesToXmlDocument(inputBytes, false);
    }


    /**
     * Convert bytes to xml document.
     *
     * @param inputBytes the input bytes
     * @param namespaeAware the namespae aware
     * @return the document
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Document convertBytesToXmlDocument(byte[] inputBytes, boolean namespaeAware) throws ParserConfigurationException, SAXException, IOException {
        InputStream inputStream = new ByteArrayInputStream(inputBytes);
        return convertInputStreamToXmlDocument(inputStream, namespaeAware);
    }


    /**
     * Convert input stream to xml document.
     *
     * @param inputStream the input stream
     * @param namespaceAware the namespace aware
     * @return the document
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Document convertInputStreamToXmlDocument(InputStream inputStream, boolean namespaceAware) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new NullXmlEntityResolver());
        return builder.parse(inputStream);
    }


    /**
     * Convert file to XML document.
     *
     * @param xmlSource the xml source
     * @return the document
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    public static Document convertFileToXMLDocument(String xmlSource) throws IOException, ParserConfigurationException, SAXException {
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader(xmlSource);
            br = new BufferedReader(fileReader);
            String strLine = null;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                stringBuilder.append(strLine);
            }
            return convertStringToXMLDocument(stringBuilder.toString());

        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
            
            if (br != null) {
                br.close();
            }
        }
    }


    /**
     * Find object in document.
     *
     * @param doc the doc
     * @param xPathLocation the x path location
     * @param qname the qname
     * @return the object
     * @throws XPathExpressionException the x path expression exception
     */
    public static Object findObjectInDocument(Document doc, String xPathLocation, QName qname) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile(xPathLocation);
        return expr.evaluate(doc, qname);
    }


    /**
     * Find object in document.
     *
     * @param doc the doc
     * @param nodeName the node name
     * @return the object
     * @throws XPathExpressionException the x path expression exception
     */
    public static Object findObjectInDocument(Document doc, String nodeName) throws XPathExpressionException {
        Object result = "";
        Element element = doc.getDocumentElement();
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName(nodeName);
            if (nodeList != null && nodeList.getLength() > 0) {
                if (nodeList.item(0).getNodeValue() == null) {
                    Node node = nodeList.item(0).getFirstChild();
                    if (node != null) {
                        result = node.getNodeValue();
                    }
                } else {
                    result = nodeList.item(0).getNodeValue();
                }
            }
        }
        return result;
    }


    /*
     * utility function to convert XML doc into Java String
     */

    /**
     * Convert document to string.
     *
     * @param document the document
     * @return the string
     * @throws ParserConfigurationException the parser configuration exception
     * @throws TransformerException the transformer exception
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String convertDocumenttoString(Document document) throws ParserConfigurationException, TransformerException, SAXException, IOException {
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }
}