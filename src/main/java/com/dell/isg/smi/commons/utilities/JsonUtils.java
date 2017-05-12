/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class.getName());

    private static ObjectMapper objectMapper = new ObjectMapper();


    private JsonUtils() {
    }


    public static final <T> T readFromString(String content, Class<T> classType) {
        try {
            return objectMapper.readValue(content, classType);
        } catch (IOException ex) {
            logger.error("readFromString error: {}", content, ex);
            return null;
        }
    }


    public static final <T> String writeToString(T value) {

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException ex) {
            logger.error("writeToString error: {}", value, ex);
        }

        return null;
    }


    public static final <T> T convertJsonResponseToObject(Class<T> clazz, Response response) {
        logger.trace("convertJsonResponseToObject() entered");
        T returnObject = null;
        ObjectMapper mapper = new ObjectMapper();
        String output = response.readEntity(String.class);
        logger.debug(" Response output {}", output);
        try {
            returnObject = (T) mapper.readValue(output, clazz);
        } catch (Exception e) {
            logger.error("error while mapping Json", e);
        } finally {
            logger.trace("convertJsonResponseToObject() exited");
        }

        return returnObject;

    }
}
