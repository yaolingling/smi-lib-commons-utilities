/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.path;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class PathUtils.
 */
public class PathUtils {

    public static final Pattern NFS_PATTERN = Pattern.compile(".*[:]{1}[/]{1}[^/].*$");
    public static final Pattern CIFS_PATTERN = Pattern.compile("[\\\\][\\\\][^\\\\].{0,255}[\\\\]{1}[^\\\\]*.*");
    public static final Pattern NFS_FILE_PATTERN = Pattern.compile(".*[:]{1}[/]{1}[^/].*[.]{1}.*$");
    public static final Pattern CIFS_FILE_PATTERN = Pattern.compile("[\\\\][\\\\][^\\\\].{0,255}[\\\\]{1}[^\\\\]*.*[.]{1}.*");


    /**
     * Checks if is NFS path.
     *
     * @param nfsPath the nfs path
     * @return true, if is NFS path
     */
    public static boolean isNFSPath(String nfsPath) {
        if (StringUtils.isNotEmpty(nfsPath) && StringUtils.isNotBlank(nfsPath)) {
            if (nfsPath.endsWith("/")) {
                nfsPath = nfsPath.substring(0, nfsPath.length() - 1);
            }
            Matcher nfsPattern = NFS_PATTERN.matcher(nfsPath);
            return nfsPattern.matches();
        }

        return false;
    }


    /**
     * Checks if is CIFS path.
     *
     * @param cifsPath the cifs path
     * @return true, if is CIFS path
     */
    public static boolean isCIFSPath(String cifsPath) {
        if (StringUtils.isNotEmpty(cifsPath) && StringUtils.isNotBlank(cifsPath)) {
            if (cifsPath.endsWith("\\")) {
                cifsPath = cifsPath.substring(0, cifsPath.length() - 1);
            }
            Matcher nfsPattern = CIFS_PATTERN.matcher(cifsPath);
            return nfsPattern.matches();
        }

        return false;
    }


    /**
     * Checks if is NFS file path.
     *
     * @param shareFilePath the share file path
     * @return true, if is NFS file path
     */
    public static boolean isNFSFilePath(String shareFilePath) {
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
    public static boolean isCIFSFilePath(String cifsFilePath) {
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
     * Checks if is valid path.
     *
     * @param path the path
     * @param domain the domain
     * @param shareUsername the share username
     * @param sharePassword the share password
     * @return true, if is valid path
     */
    public static boolean isValidPath(String path, String domain, String shareUsername, String sharePassword) {

        if (StringUtils.isNotEmpty(path) && StringUtils.isNotBlank(path)) {
            if (isNFSPath(path)) {
                return true;
            } else if (isCIFSPath(path)) {
                // validate if the username and password are provided or not.
                return validateCifsUserinfo(domain, shareUsername, sharePassword);
            }
        } else {
            return false;
            // throw new IllegalArgumentException("Share Path");
            // ExceptionUtilities.handleInvalidArgs("Share Path");
        }

        return false;
    }


    /**
     * Checks if is valid file path.
     *
     * @param path the path
     * @param domain the domain
     * @param shareUsername the share username
     * @param sharePassword the share password
     * @return true, if is valid file path
     */
    public static boolean isValidFilePath(String path, String domain, String shareUsername, String sharePassword) {

        if (StringUtils.isNotEmpty(path) && StringUtils.isNotBlank(path)) {
            if (isNFSFilePath(path)) {
                return true;
            } else if (isCIFSFilePath(path)) {
                // validate if the username and password are provided or not.
                return validateCifsUserinfo(domain, shareUsername, sharePassword);
            }
        } else {
            return false;
            // throw new IllegalArgumentException("Share Path");
            // ExceptionUtilities.handleInvalidArgs("Share Path");
        }

        return false;
    }


    /**
     * Validate cifs userinfo.
     *
     * @param checkDomain the check domain
     * @param checkUser the check user
     * @param checkPassword the check password
     * @return true, if successful
     */
    public static boolean validateCifsUserinfo(String checkDomain, String checkUser, String checkPassword) {
        return true;
    }

}
