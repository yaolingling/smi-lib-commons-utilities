/**
 * Copyright � 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.fileshare;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.isg.smi.commons.model.fileshare.FileShare;
import com.dell.isg.smi.commons.model.fileshare.FileShareTypeEnum;
import com.dell.isg.smi.commons.utilities.command.CommandResponse;
import com.dell.isg.smi.commons.utilities.command.IssueCommands;
import com.dell.isg.smi.commons.utilities.properties.PropertyFileReader;

/**
 * The Class FileShareService.
 */
public class FileShareService {

    private static final Logger logger = LoggerFactory.getLogger(FileShareService.class.getName());

    public static final Pattern CIFS_PATTERN = Pattern.compile("[\\\\][\\\\][^\\\\].{0,255}[\\\\]{1}[^\\\\]*.*");
    public static final Pattern NFS_FILE_PATTERN = Pattern.compile(".*[:]{1}[/]{1}[^/].*[.]{1}.*$");
    public static final Pattern CIFS_FILE_PATTERN = Pattern.compile("[\\\\][\\\\][^\\\\].{0,255}[\\\\]{1}[^\\\\]*.*[.]{1}.*");


    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public String getProperties() {
        String result;
        try {
            Properties properties = PropertyFileReader.getInstance().getProperties();
            return properties.toString();
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }


    /**
     * Gets the share types.
     *
     * @return the share types
     */
    public List<FileShareTypeEnum> getShareTypes() {
        List<FileShareTypeEnum> types = new ArrayList<>();
        types.add(FileShareTypeEnum.CIFS);
        types.add(FileShareTypeEnum.NFS);
        return types;
    }


    /**
     * Checks if is valid file share.
     *
     * @param fileshare the fileshare
     * @return the boolean
     */
    public Boolean isValidFileShare(FileShare fileshare) {
        if (StringUtils.isNotEmpty(fileshare.getPath()) && StringUtils.isNotBlank(fileshare.getPath())) {
            // If the file share object is null or doesn't include a path,
            // return an invalid data error for the path field
            // RuntimeCoreException.handleRuntimeCoreException(EnumErrorCode.ENUM_INVALID_DATA, "Share Path");
            return false;
        }

        if (isNFSFilePath(fileshare.getPath())) {
            return true;
        } else if (isCIFSFilePath(fileshare.getPath())) {
            // validate if the username and password are provided or not.
            return validateCifsUserinfo(fileshare.getPasswordCredential().getUsername(), fileshare.getPasswordCredential().getPassword());
        }
        return false;
    }


    /**
     * Checks if is NFS file path.
     *
     * @param shareFilePath the share file path
     * @return true, if is NFS file path
     */
    private static boolean isNFSFilePath(String shareFilePath) {
        if (shareFilePath != null) {
            return NFS_FILE_PATTERN.matcher(shareFilePath).matches();
        }
        return false;
    }


    /**
     * Checks if is CIFS file path.
     *
     * @param cifsFilePath the cifs file path
     * @return true, if is CIFS file path
     */
    private static boolean isCIFSFilePath(String cifsFilePath) {
        if (StringUtils.isNotEmpty(cifsFilePath) && StringUtils.isNotBlank(cifsFilePath)) {
            if (cifsFilePath.endsWith("\\")) {
                cifsFilePath = cifsFilePath.substring(0, cifsFilePath.length() - 1);
            }
            Matcher nfsPattern = CIFS_FILE_PATTERN.matcher(cifsFilePath);
            return nfsPattern.matches();
        }

        return false;
    }


    /**
     * Validate cifs userinfo.
     *
     * @param checkUser the check user
     * @param checkPassword the check password
     * @return true, if successful
     */
    private static boolean validateCifsUserinfo(String checkUser, String checkPassword) {
        // final String AT = "@";
        // final String PERCENT = "%";
        boolean result = true;

        // if (checkUser == null || checkUser.isEmpty() ||
        // checkUser.contains(AT) || checkDomain.contains(PERCENT)) {
        // ExceptionUtilities.handleRuntimeSpectre(2008);
        // }
        // if (checkPassword == null || checkPassword.isEmpty() ||
        // checkPassword.contains(AT) || checkPassword.contains(PERCENT) ) {
        // ExceptionUtilities.handleRuntimeSpectre(2007);
        // }
        //
        // if ( checkDomain != null && ( checkDomain.contains(AT) ||
        // checkDomain.contains(PERCENT) ) )
        // {
        // ExceptionUtilities.handleRuntimeSpectre(2009);
        // }

        return result;
    }


    /**
     * Mount.
     *
     * @param fileShare the file share
     * @return the boolean
     */
    public Boolean mount(FileShare fileShare) {
        logger.debug("Inside of " + FileShareService.class.getName() + "mount()");
        if (null != fileShare) {
            String scriptName = fileShare.getScriptName();
            String scriptDirectory = fileShare.getScriptDirectory();
            if (StringUtils.isNotBlank(scriptName) && StringUtils.isNotBlank(scriptDirectory)) {
                if (scriptDirectory.charAt(scriptDirectory.length() - 1) != '/') {
                    scriptDirectory += Character.toString('/');
                }
                String script = scriptDirectory + scriptName;
                List<String> cmd = new ArrayList<>();
                // cmd.add("sudo");
                cmd.add(script);
                cmd.add("--name");
                cmd.add(fileShare.getName());
                cmd.add("--address");
                cmd.add(fileShare.getAddress());
                cmd.add("--path");
                cmd.add(fileShare.getPath());
                cmd.add("--type");
                cmd.add(fileShare.getType().toString());

                if (fileShare.getType().toString() == FileShareTypeEnum.CIFS.toString()) {
                    cmd.add("--username");
                    cmd.add(fileShare.getPasswordCredential().getUsername());
                    cmd.add("--password");
                    cmd.add(fileShare.getPasswordCredential().getPassword());
                }

                CommandResponse commandResponse;
                try {                    
                    commandResponse = IssueCommands.issueSystemCommand(cmd.toArray(new String[cmd.size()]));
                    if (Integer.parseInt(commandResponse.getReturnCode()) != 0) {
                        logger.error("Failed to mount {} share: {}", fileShare.getType().toString(), fileShare.getName());
                        return false;
                    }
                } catch (Exception e) {
                    logger.error("Failed to mount {} share: {}", fileShare.getType().toString(), fileShare.getName(), e);
                    return false;
                }
                // log the return message
                if (!StringUtils.isEmpty(commandResponse.getReturnMessage())) {
                    logger.debug(commandResponse.getReturnMessage());
                }

            } else {
                logger.info("Unable to mount because script name and directory is empty");
                return false;
            }

        } else {
            logger.info("Unable to mount because Null Fileshare details");
            return false;
        }
        return true;
    }


    /**
     * Unmount.
     *
     * @param fileShare the fileShare object
     * @return the boolean
     */
    public Boolean unmount(FileShare fileShare) {
    	logger.debug("Inside of " + FileShareService.class.getName() + "umount()");
    	
    	if (null != fileShare) {
    		String scriptName = fileShare.getScriptName();
    		String scriptDirectory = fileShare.getScriptDirectory();
    		if (StringUtils.isNotBlank(scriptName) && StringUtils.isNotBlank(scriptDirectory)) {
    			if (scriptDirectory.charAt(scriptDirectory.length() - 1) != '/') {
                    scriptDirectory += Character.toString('/');
                }
    			String script = scriptDirectory + scriptName;
                List<String> cmd = new ArrayList<>();
                cmd.add(script);
                cmd.add("--unmount");
                cmd.add(fileShare.getName());
                
                CommandResponse commandResponse;
                try {
                	commandResponse = IssueCommands.issueSystemCommand(cmd.toArray(new String[0]));
                } catch (Exception e) {
                	logger.error("Failed to un-mount {} share: {}", fileShare.getName(), e);
                	return false;
                }   			
    		}
    	} else {
    		logger.info("Unable to mount because Null Fileshare details");
    		return false;
    	}
    	return true;
    }
}
