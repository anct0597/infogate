package vn.infogate.ispider.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * @author an.cantuong
 * created at 11/13/2021
 */
public final class DatetimeUtils {

    public static final String VI_DATE_DD_MM_YYYY = "dd/MM/YYYY";

    public static final String YESTERDAY = "hôm qua";
    public static final String CURRENT = "hôm nay";
    public static final String DAY = "ngày";
    public static final String HOUR = "giờ";
    public static final String MINUTE = "phút";

    public static long getCurTimeAsMs() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static long getTimeAsMs(String date) {
        var dateTime = LocalDateTime.parse(date,
                DateTimeFormatter.ofPattern(VI_DATE_DD_MM_YYYY));
        return dateTime.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
    }

    public static long getTimeBefore(int before, String unit) {
        if (DAY.equals(unit)) {
            return getTimeBeforeDate(before);
        } else if (HOUR.equals(unit)) {
            return getTimeBeforeHours(before);
        }
        return getTimeBeforeMinutes(before);
    }


    public static long getTimeBeforeDate(int date) {
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, date);
        return calendar.getTimeInMillis();
    }

    public static long getTimeBeforeHours(int hours) {
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTimeInMillis();
    }

    public static long getTimeBeforeMinutes(int minutes) {
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTimeInMillis();
    }
}
