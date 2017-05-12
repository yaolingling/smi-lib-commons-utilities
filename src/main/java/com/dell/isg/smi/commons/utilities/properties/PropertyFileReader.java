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
 * The Class PropertyFileReader.
 *
 * @author Michael_Regert
 */
public class PropertyFileReader {
    private static final Logger logger = LoggerFactory.getLogger(PropertyFileReader.class.getName());

    private String propertyFileName = "application.properties";
    private Properties properties = null;
    private static PropertyFileReader instance = null;


    /**
     * Instantiates a new property file reader.
     */
    protected PropertyFileReader() {
        // Exists only to prevent instantiation of this class
    }

    
    /**
     * Gets the single instance of PropertyFileReader.
     *
     * @return single instance of PropertyFileReader
     */
    public static PropertyFileReader getInstance() {
        if (instance == null) {
            instance = new PropertyFileReader();
        }
        return instance;
    }

    
    /**
     * Gets the properties.
     *
     * @return the properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Properties getProperties() throws IOException {
        try {
            getProperties(false);
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
        return properties;
    }


    /**
     * Gets the properties.
     *
     * @param refresh the refresh
     * @return the properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Properties getProperties(boolean refresh) throws IOException {
        if (properties != null && !refresh) {
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


    /**
     * Gets the script name with path.
     *
     * @return the script name with path
     */
    public static String getScriptNameWithPath() {
        String scriptName = "";
        try {
            PropertyFileReader reader = PropertyFileReader.getInstance();
            Properties properties = reader.getProperties();
            String scriptDirectory = properties.getProperty("script_directory");

            if (scriptDirectory.charAt(scriptDirectory.length() - 1) != '/') {
                scriptDirectory += Character.toString('/');
            }
            scriptName = scriptDirectory + properties.getProperty("script_name");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return scriptName;
    }
}
