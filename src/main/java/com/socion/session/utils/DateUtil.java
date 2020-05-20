package com.socion.session.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

	private DateUtil() {
	}

	public static Boolean validateDateTimeFormat(String dateTime) {
        if (!StringUtils.isEmpty(dateTime)) {
            String regex = "[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(dateTime);
            return matcher.matches();
        }
        return false;
    }

    public static Boolean validateDateTimeFormatMS(String dateTime) {
        if (!StringUtils.isEmpty(dateTime)) {
            String regex = "[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(dateTime);
            return matcher.matches();
        }
        return false;
    }

    public static Boolean isCurrentDateTimeAfterADate(Long sessionTimeMS, String offsetMinutes) {
        long milliSecondsOffset = 0;
        if (null != offsetMinutes && !offsetMinutes.isEmpty()) {
            milliSecondsOffset = (long) (Double.parseDouble(offsetMinutes) * 60 * 1000);
        }
        LOGGER.info("isCurrentDateTimeAfterADate:{}{}{}",sessionTimeMS , System.currentTimeMillis() , milliSecondsOffset);
        return sessionTimeMS <= (System.currentTimeMillis());
    }

    public static Boolean isCurrentDateAndTimeAfterADate(String time) {
        String[] sessionStartDate = time.split(" ");
        return (LocalDate.parse(sessionStartDate[0]).isEqual(LocalDate.now()) && LocalTime.now().isAfter(LocalTime.parse(sessionStartDate[1]))) || LocalDate.now().isAfter(LocalDate.parse(sessionStartDate[0]));
    }

    public static Boolean isCurrentDateAndTimeBeforeADate(String time) {
        String[] sessionStartDate = time.split(" ");
        return (LocalDate.parse(sessionStartDate[0]).isEqual(LocalDate.now()) && LocalTime.now().isBefore(LocalTime.parse(sessionStartDate[1]))) || LocalDate.now().isBefore(LocalDate.parse(sessionStartDate[0]));
    }


    public static String fetchTheLocalTimeForNotification(String datetime, Long offsetMinutes) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
		long sessionTimeMS = 0;
        try {
            date = format.parse(datetime);
            sessionTimeMS = date.getTime();
        } catch (ParseException e) {
			LOGGER.info("Exception:{}",e.getMessage());
        }

        long milliSecondsOffset = 0;
        if (null != offsetMinutes) {
            milliSecondsOffset = (offsetMinutes) * 60 * 1000;
        }

        sessionTimeMS = sessionTimeMS - milliSecondsOffset;
        Date currentDate = new Date(sessionTimeMS);

        return format.format(currentDate);

    }

}
