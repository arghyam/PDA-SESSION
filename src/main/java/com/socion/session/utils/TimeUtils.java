package com.socion.session.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimeUtils {

    private TimeUtils() {
    }

    public static String formatDateTime(String dateTime, String timeZone) throws ParseException {
        String outputTime = null;
        if (null != timeZone) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            inputFormat.setTimeZone(TimeZone.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy KK:mm a");
            outputFormat.setTimeZone(TimeZone.getTimeZone(timeZone.substring(0, 8)));
            outputTime = outputFormat.format(inputFormat.parse(dateTime)) + " " + timeZone.substring(10, timeZone.length() - 1);

        } else {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            inputFormat.setTimeZone(TimeZone.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy KK:mm a z");
            outputTime = outputFormat.format(inputFormat.parse(dateTime));
        }

        return "(" + outputTime + ")";
    }

    public static String convertToUTCTImeZoneAddMinutes(String dateTime,int minutesToAdd) throws ParseException {
        String finalTime = null;
        DateFormat inputFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        inputFormat.setTimeZone(TimeZone.getDefault());
        DateTimeFormatter f1 = DateTimeFormat.forPattern(Constants.DATE_FORMAT);
        DateTime finalDates = f1.parseDateTime(dateTime).plusMinutes(minutesToAdd);
        finalTime = finalDates.toString().substring(0, 10) + " " + finalDates.toString().substring(11, 23);
        return finalTime;
    }



    public static String convertToUTCTImeZone(String dateTime, int minutesToAdd) throws ParseException {
        String finalTime = null;
        DateFormat inputFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        inputFormat.setTimeZone(TimeZone.getDefault());
        DateTimeFormatter f1 = DateTimeFormat.forPattern(Constants.DATE_FORMAT);
        DateTime finalDates = f1.parseDateTime(dateTime).plusMinutes(minutesToAdd);
        finalTime = finalDates.toString().substring(0, 10) + " " + finalDates.toString().substring(11, 23);
        return finalTime;
    }

    public static String getSessionEndDayStart(String dateTime, String timeZone) throws ParseException {
        String outputTime = null;
        String finalTime = null;
        if (null != timeZone) {
            DateFormat inputFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            inputFormat.setTimeZone(TimeZone.getDefault());
            DateFormat outputFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            outputFormat.setTimeZone(TimeZone.getTimeZone(timeZone.substring(0, 8)));
            outputTime = outputFormat.format(inputFormat.parse(dateTime));
            DateTimeFormatter f = DateTimeFormat.forPattern(Constants.DATE_FORMAT);
            DateTime finalDate = f.parseDateTime(outputTime);
            DateTimeFormatter f1 = DateTimeFormat.forPattern(Constants.DATE_FORMAT);
            DateTime finalDates = f1.parseDateTime(dateTime).minusMillis(finalDate.getMillisOfDay());
            finalTime = finalDates.toString().substring(0, 10) + " " + finalDates.toString().substring(11, 23);

        } else {
            finalTime = dateTime;
        }
        return finalTime;
    }

    public static String changeTimeZones(String dateTime, String timeZone) throws ParseException {
        String outputTime = null;
        if (null != timeZone) {
            DateFormat inputFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            inputFormat.setTimeZone(TimeZone.getDefault());
            DateFormat outputFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            outputFormat.setTimeZone(TimeZone.getTimeZone(timeZone.substring(0, 8)));
            outputTime = outputFormat.format(inputFormat.parse(dateTime));

        } else {
            outputTime = dateTime;
        }
        return outputTime;
    }

}
