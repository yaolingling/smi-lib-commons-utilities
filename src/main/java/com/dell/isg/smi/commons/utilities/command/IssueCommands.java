/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.isg.smi.commons.utilities.stream.StreamUtils;

public class IssueCommands {

    private static final Logger logger = LoggerFactory.getLogger(IssueCommands.class);


    public synchronized static CommandResponse issueSystemCommand(String command) throws Exception {
        logger.trace("entered issueSystemCommand(String)");

        final String ERROR_CODE = "-1";
        CommandResponse response = new CommandResponse();
        response.setReturnCode(ERROR_CODE);
        response.setReturnMessage("");

        try {
            Runtime runtime = Runtime.getRuntime();
            if (runtime != null) {
                logger.debug("++++++++++ Lets execute system command +++++++++");
                Process process = runtime.exec(command);
                logger.debug("+++++++++++ System Command Executed ++++++++++++");
                if (process != null) {
                    logger.debug("++++++++++ Got the process object ++++++++++");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    if (reader != null) {
                        logger.debug("++++++++ Got the output stream +++++++++");
                        StringBuilder output = new StringBuilder("");
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            output.append(line);
                        }

                        StreamUtils.closeStreamQuietly(reader);
                        process.waitFor();
                        response.setReturnMessage(output.toString());
                        response.setReturnCode(Integer.toString(process.exitValue()));
                    }
                } else {
                    logger.error("Unable to get prcess object from runtime.exec().");
                }
            } else {
                logger.error("Unable to get java Runtime.");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        logger.trace("exiting issueSystemCommand(String)");
        return response;
    }


    // command array
    public synchronized static CommandResponse issueSystemCommand(String[] command) throws Exception {
        logger.trace("entered issueSystemCommand(String[])");

        final String ERROR_CODE = "-1";
        CommandResponse response = new CommandResponse();
        response.setReturnCode(ERROR_CODE);
        response.setReturnMessage("");

        try {
            Runtime runtime = Runtime.getRuntime();
            if (runtime != null) {
                logger.info("++++++++++ Lets execute system command +++++++++");
                Process process = runtime.exec(command);
                logger.info("+++++++++++ System Command Executed ++++++++++++");
                if (process != null) {
                    logger.info("++++++++++ Got the process object ++++++++++");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    if (reader != null) {
                        logger.info("++++++++ Got the output stream +++++++++");
                        StringBuilder output = new StringBuilder("");
                        String line = null;
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
                            String errorLine = null;
                            while ((errorLine = errorBufferedReader.readLine()) != null) {
                                error.append(errorLine);
                                error.append(System.lineSeparator());
                            }
                            response.setReturnMessage(error.toString());
                        }
                        response.setReturnCode(Integer.toString(process.exitValue()));
                    }
                } else {
                    logger.error("Unable to get prcess object from runtime.exec().");
                }
            } else {
                logger.error("Unable to get java Runtime.");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        logger.trace("exiting issueSystemCommand(String)");
        return response;
    }
}
