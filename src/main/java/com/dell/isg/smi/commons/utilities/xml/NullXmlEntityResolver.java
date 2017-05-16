/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
/**
 * 
 */
package com.dell.isg.smi.commons.utilities.xml;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Michael_Hepfer
 *
 * To prevent - XML External Entity Injection (Input Validation and Representation, Control flow) This class doesn't allow resolution of xml to entities and when used can preven
 * XML External Entity Attacks.
 */
public class NullXmlEntityResolver implements EntityResolver {
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return new InputSource(new StringReader(""));
    }
}