/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.isg.smi.commons.utilities.stream.StreamUtils;

/**
 * The Class IssueCommands.
 */
public class IssueCommands {

    private static final Logger logger = LoggerFactory.getLogger(IssueCommands.class);

    private IssueCommands(){}

    /**
     * Issue system command.
     *
     * @param command the command
     * @return the command response
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException the interrupted exception
     */
    public static synchronized CommandResponse issueSystemCommand(String command) throws IOException, InterruptedException {
        logger.trace("entered issueSystemCommand(String)");

        final String errorCode = "-1";
        CommandResponse response = new CommandResponse();
        response.setReturnCode(errorCode);
        response.setReturnMessage("");
        
        BufferedReader reader = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            if (runtime != null) {
                logger.debug("++++++++++ Lets execute system command +++++++++");
                Process process = runtime.exec(command);
                logger.debug("+++++++++++ System Command Executed ++++++++++++");
                if (process != null) {
                    logger.debug("++++++++++ Got the process object ++++++++++");
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    
                    logger.debug("++++++++ Got the output stream +++++++++");
                    StringBuilder output = new StringBuilder("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line);
                    }

                    process.waitFor();
                    response.setReturnMessage(output.toString());
                    response.setReturnCode(Integer.toString(process.exitValue()));
                } else {
                    logger.error("Unable to get prcess object from runtime.exec().");
                }
            } else {
                logger.error("Unable to get java Runtime.");
            }
        } finally{
            StreamUtils.closeStreamQuietly(reader);  
        }

        logger.trace("exiting issueSystemCommand(String)");
        return response;
    }


    /**
     * Issue system command.
     *
     * @param command the command
     * @return the command response
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException the interrupted exception
     */
    // command array
    public static synchronized CommandResponse issueSystemCommand(String[] command) throws IOException, InterruptedException {
        logger.trace("entered issueSystemCommand(String[])");

        final String errorCode = "-1";
        CommandResponse response = new CommandResponse();
        response.setReturnCode(errorCode);
        response.setReturnMessage("");

        BufferedReader reader = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            if (runtime != null) {
                logger.info("++++++++++ Lets execute system command +++++++++");
                Process process = runtime.exec(command);
                logger.info("+++++++++++ System Command Executed ++++++++++++");
                if (process != null) {
                    logger.info("++++++++++ Got the process object ++++++++++");
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    
                    logger.info("++++++++ Got the output stream +++++++++");
                    StringBuilder output = new StringBuilder("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line);
                        output.append(System.lineSeparator());
                    }

                    StreamUtils.closeStreamQuietly(reader);
                    process.waitFor();
                    if (!StringUtils.isEmpty(output)) {
                        response.setReturnMessage(output.toString());
                    } else {
                        BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        StringBuilder error = new StringBuilder("");
                        String errorLine;
                        while ((errorLine = errorBufferedReader.readLine()) != null) {
                            error.append(errorLine);
                            error.append(System.lineSeparator());
                        }
                        response.setReturnMessage(error.toString());
                    }
                    response.setReturnCode(Integer.toString(process.exitValue()));
                } else {
                    logger.error("Unable to get prcess object from runtime.exec().");
                }
            } else {
                logger.error("Unable to get java Runtime.");
            }
        } finally{
            StreamUtils.closeStreamQuietly(reader);  
        }

        logger.trace("exiting issueSystemCommand(String)");
        return response;
    }
}
