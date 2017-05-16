/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities.datetime;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DateTimeUtils.
 */
public class DateTimeUtils {

    public static final String PATTERN_FULL = "MM/dd/yyyy hh:mm:ss a";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class.getName());


    private DateTimeUtils() {
    }


    /**
     * Transform a date in a long to a GregorianCalendar.
     *
     * @param date the date
     * @return the XML gregorian calendar
     * @throws DatatypeConfigurationException the datatype configuration exception
     */
    public static XMLGregorianCalendar long2Gregorian(long date) throws DatatypeConfigurationException {
        DatatypeFactory dataTypeFactory;
        dataTypeFactory = DatatypeFactory.newInstance();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date);
        return dataTypeFactory.newXMLGregorianCalendar(gc);
    }


    /**
     * Transform a date in Date to XMLGregorianCalendar.
     *
     * @param date the date
     * @return the XML gregorian calendar
     * @throws DatatypeConfigurationException the datatype configuration exception
     */
    public static XMLGregorianCalendar date2Gregorian(Date date) throws DatatypeConfigurationException {
        return (date == null) ? null : long2Gregorian(date.getTime());
    }


    /**
     * Returns current UTC date.
     *
     * @return the utc date
     */
    public static Date getUtcDate() {
        return getUtcDate(new Date());
    }


    /**
     * Returns the UTC date corresponding to the non UTC date passed in.
     *
     * @param nonUtcDate the non utc date
     * @return UTC date
     */
    public static Date getUtcDate(Date nonUtcDate) {
        if (nonUtcDate != null) {
            int timeZoneOffset = TimeZone.getDefault().getOffset(nonUtcDate.getTime());
            Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            utcCal.setTimeInMillis(nonUtcDate.getTime() - timeZoneOffset);
            return utcCal.getTime();
        }
        return null;
    }


    /**
     * This method will try to form the java.util.Date object from the string representation of the date. The date patterns specified in the datePatterns will be used to see the
     * best fit.
     *
     * @param dateString the date string
     * @param datePatterns the date patterns
     * @return the corresponding utc date
     * @throws ParseException the parse exception
     */
    public static Date getCorrespondingUtcDate(String dateString, String[] datePatterns) throws ParseException {
        Date dt = null;
        if (dateString == null) {
            return null;
        }
        for (String datePattern : datePatterns) {
            dt = getUtcDateFromString(datePattern, dateString);
            if (dt != null) {
                break;
            }
        }
        return dt;
    }


    /**
     * Gets the utc date from string.
     *
     * @param pattern the pattern
     * @param strDate the str date
     * @return the utc date from string
     * @throws ParseException the parse exception
     */
    public static Date getUtcDateFromString(String pattern, String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        formatter.setLenient(false);
        return formatter.parse(strDate);
    }


    /**
     * Gets the iso date string.
     *
     * @param date the date
     * @return the iso date string
     */
    public static String getIsoDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }


    /**
     * Gets the iso date string.
     *
     * @param timeMillis the time millis
     * @return the iso date string
     */
    public static String getIsoDateString(long timeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  dateFormat.format(timeMillis);
    }


    /**
     * Gets the iso date string.
     *
     * @param utcDateString the utc date string
     * @param datePatterns the date patterns
     * @return the iso date string
     * @throws ParseException the parse exception
     */
    public static String getIsoDateString(String utcDateString, String[] datePatterns) throws ParseException {
        Date date = getCorrespondingUtcDate(utcDateString, datePatterns);
        return getIsoDateString(date);
    }


    /**
     * Gets the iso date string.
     *
     * @param dateString the date string
     * @param dateFormat the date format
     * @return the iso date string
     * @throws ParseException the parse exception
     */
    public static String getIsoDateString(String dateString, String dateFormat) throws ParseException {
        Date date;
        date = getUtcDateFromString(dateFormat, dateString);
        return DateTimeUtils.getIsoDateString(date);
    }


    /**
     * Gets the current time.
     *
     * @return the current time
     */
    public static String getCurrentTime() {
        return DateTimeUtils.getIsoDateString(new Date());
    }


    /**
     * String to sql date.
     *
     * @param date the date
     * @return the java.sql. date
     * @throws ParseException the parse exception
     */
    public static java.sql.Date stringToSqlDate(String date) throws ParseException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
        java.util.Date createdDate = sdf1.parse(date);
        return new java.sql.Date(createdDate.getTime());
    }


    /**
     * String to timestamp.
     *
     * @param dateString the date string
     * @return the timestamp
     */
    public static Timestamp stringToTimestamp(String dateString) {
        return stringToTimestamp(dateString, ISO_DATE_FORMAT);
    }


    /**
     * String to timestamp.
     *
     * @param dateString the date string
     * @param dateFormat the date format
     * @return the timestamp
     */
    public static Timestamp stringToTimestamp(String dateString, String dateFormat) {
        Timestamp sqlCreatedDate = null;
        try {
            Date createdDate = new SimpleDateFormat(dateFormat).parse(dateString);
            sqlCreatedDate = new Timestamp(createdDate.getTime());
        } catch (Exception ex) {
            logger.error("Parse date failed", ex);
            return null;
        }
        return sqlCreatedDate;
    }


    /**
     * String to time.
     *
     * @param timeString the time string
     * @return the time
     * @throws ParseException the parse exception
     */
    public static Time stringToTime(String timeString) throws ParseException {
        if (StringUtils.isBlank(timeString)) {
            return null;
        }

        DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Date date = sdf.parse(timeString);
        return new Time(date.getTime());
    }


    /**
     * Time to string.
     *
     * @param time the time
     * @return the string
     */
    public static String timeToString(Time time) {
        if (time == null) {
            return null;
        }
        DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(time);
    }
}
