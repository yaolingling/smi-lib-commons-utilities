/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * The Class XMLUtil.
 */
public class XMLUtil {

    private XMLUtil(){ 
    }
    
    /**
     * Parses an xml from the filesystem using JAXB.
     *
     * @param xmlPath the xml path
     * @param jaxbModelClasses the jaxb model classes
     * @return the object
     * @throws JAXBException the JAXB exception
     */
    public static Object parseXML(String xmlPath, Class[] jaxbModelClasses) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(jaxbModelClasses);
        Unmarshaller u = jc.createUnmarshaller();
        Object xmlElement = null;
        try {
            xmlElement = u.unmarshal(new File(xmlPath));
        } catch (JAXBException e) {
            throw e;
        }
        return xmlElement;
    }


    /**
     * Parses an xml from the filesystem using JAXB and ignores specified namespace. This is requred when we are parsing an xml file which does not specify namespace and the same
     * needs to be unmarshalled
     *
     * @param xmlPath the xml path
     * @param namespace the namespace
     * @param jaxbModelClasses the jaxb model classes
     * @return the object
     * @throws JAXBException the JAXB exception
     * @throws SAXException the SAX exception
     * @throws ParserConfigurationException the parser configuration exception
     * @throws FileNotFoundException the file not found exception
     */
    public static Object parseXMLIgnoreNamespace(String xmlPath, String namespace, Class[] jaxbModelClasses) throws JAXBException, SAXException, ParserConfigurationException, FileNotFoundException {
        JAXBContext jc = JAXBContext.newInstance(jaxbModelClasses);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        // Create the XMLReader
        SAXParserFactory factory = SAXParserFactory.newInstance();
        XMLReader reader = factory.newSAXParser().getXMLReader();
        // The filter class to set the correct namespace
        XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader, namespace);
        reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
        InputStream inStream = new FileInputStream(new File(xmlPath));
        SAXSource source = new SAXSource(xmlFilter, new InputSource(inStream));
        // Get the element
        Object xmlElement = unmarshaller.unmarshal(source);
        return xmlElement;
    }


    /**
     * Parses the XML from stream.
     *
     * @param xmlData the xml data
     * @param jaxbModelClasses the jaxb model classes
     * @return the object
     * @throws JAXBException the JAXB exception
     */
    public static Object parseXMLFromStream(InputStream xmlData, Class[] jaxbModelClasses) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(jaxbModelClasses);
        Unmarshaller u = jc.createUnmarshaller();
        return u.unmarshal(xmlData);
    }


    /**
     * Saves the xml using jaxb implementation.
     *
     * @param xmlPath the xml path
     * @param jaxbModelClasses the jaxb model classes
     * @param element the element
     * @throws JAXBException the JAXB exception
     */
    public static void saveXML(String xmlPath, Class[] jaxbModelClasses, Object element) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(jaxbModelClasses);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(element, new File(xmlPath));
    }


    /**
     * Validate.
     *
     * @param schemaFile the schema file
     * @param buf the buf
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void validate(InputStream schemaFile, byte[] buf) throws SAXException, IOException {
        Source xmlFile = new StreamSource(new ByteArrayInputStream(buf));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(schemaFile));
        Validator validator = schema.newValidator();
        validator.validate(xmlFile);
    }


    /**
     * Gets the xml.
     *
     * @param object the object
     * @param jaxbClasses the jaxb classes
     * @return the xml
     * @throws JAXBException the JAXB exception
     */
    public static byte[] getXML(Object object, Class[] jaxbClasses) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(jaxbClasses);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream sbos = new ByteArrayOutputStream();
        m.marshal(object, sbos);
        return sbos.toByteArray();
    }

    /**
     * XMLNamespaceFilter.
     */
    public static class XMLNamespaceFilter extends XMLFilterImpl {
        private String namespace;


        /**
         * Instantiates a new XML namespace filter.
         *
         * @param arg0 the arg 0
         * @param namespace the namespace
         */
        public XMLNamespaceFilter(XMLReader arg0, String namespace) {
            super(arg0);
            this.namespace = namespace;
        }


        /* (non-Javadoc)
         * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(this.namespace, localName, qName, attributes);
        }
    }


    /**
     * Convert file to XML string.
     *
     * @param xmlSource the xml source
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String convertFileToXMLString(String xmlSource) throws IOException {
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
            return stringBuilder.toString();
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
            
            if (br != null) {
                br.close();
            }
        }
    }
}
