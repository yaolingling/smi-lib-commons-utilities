/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.stream;

import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class StreamUtils.
 */
public class StreamUtils {
    private static final Logger log = LoggerFactory.getLogger(StreamUtils.class);


    private StreamUtils() {
    }


    /**
     * Close stream quietly.
     *
     * @param closable the closable
     */
    public static void closeStreamQuietly(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (NullPointerException e) {
                log.warn("Can't close stream - NPE", e);
            } catch (RuntimeException e) {
                log.warn("Can't close stream - RE", e);
            } catch (Exception e) {
                log.warn("Can't close stream - E", e);
            }
        }
    }
}
