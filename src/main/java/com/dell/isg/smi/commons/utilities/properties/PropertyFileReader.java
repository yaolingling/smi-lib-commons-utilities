/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael_Regert
 *
 */
public class PropertyFileReader {
    private static final Logger logger = LoggerFactory.getLogger(PropertyFileReader.class.getName());

    private String propertyFileName = "application.properties";
    private Properties properties = null;
    private static PropertyFileReader instance = null;


    /*-----------------------------------------------------------------------*/

    public static PropertyFileReader getInstance() {
        if (instance == null) {
            instance = new PropertyFileReader();
        }
        return instance;
    }


    /*-----------------------------------------------------------------------*/

    public Properties getProperties() throws IOException {
        try {
            getProperties(false);
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
        return properties;
    }


    /*-----------------------------------------------------------------------*/

    public Properties getProperties(boolean refresh) throws IOException {
        if (properties != null && refresh == false) {
            return properties;
        }

        properties = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName);
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file '" + propertyFileName + "' not found");
            }
        }

        catch (Exception e) {
            logger.error("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
        }
        return properties;
    }


    /*-----------------------------------------------------------------------*/

    public static String getScriptNameWithPath() {
        String scriptName = "";
        try {
            PropertyFileReader reader = PropertyFileReader.getInstance();
            Properties properties = reader.getProperties();
            String scriptDirectory = properties.getProperty("script_directory");

            if (scriptDirectory.charAt(scriptDirectory.length() - 1) != '/') {
                scriptDirectory += '/';
            }
            scriptName = scriptDirectory + properties.getProperty("script_name");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return scriptName;
    }


    /*-----------------------------------------------------------------------*/

    protected PropertyFileReader() {
        // Exists only to prevent instantiation of this class
    }
}
