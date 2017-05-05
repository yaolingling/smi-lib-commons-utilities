/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.stream;

import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamUtils {
    private final static Logger log = LoggerFactory.getLogger(StreamUtils.class);


    public static void closeStreamQuietly(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (NullPointerException e) {
                log.warn("Can't close stream");
            } catch (RuntimeException e) {
                log.warn("Can't close stream");
            } catch (Exception e) {
                log.warn("Can't close stream");
            } catch (Throwable ex) {
                log.warn("Can't close stream");
            }
        }
    }
}
