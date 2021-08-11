package org.jalicz.CTF.OutGameData;

import org.jalicz.CTF.Enums.TimeUnit;

import java.time.LocalDateTime;

public class RealTime {

    private static LocalDateTime time = LocalDateTime.now();
    private static final long
            minuteSec = 60,
            hourSec = minuteSec*60,
            daySec = hourSec*24,
            weekSec = daySec*7,
            monthSec = Math.round(daySec*29.6041667),
            yearSec = monthSec*12;


    private static void update() {
        time = LocalDateTime.now();
    }

    public static long getTotalSeconds() {
        update();
        return (time.getSecond() + time.getMinute()*minuteSec + time.getHour()*hourSec + time.getDayOfMonth()*daySec + time.getMonthValue()*monthSec + time.getYear()*yearSec);
    }

    public static long convertToSeconds(long value, TimeUnit unit) {
        try {
            long totalSec = getTotalSeconds();
            switch (unit) {
                case SECONDS: return totalSec + value;
                case MINUTES: return totalSec + value*minuteSec;
                case HOURS:   return totalSec + value*hourSec;
                case DAYS:    return totalSec + value*daySec;
                case WEEKS:   return totalSec + value*weekSec;
                case MONTHS:  return totalSec + value*monthSec;
                case YEARS:   return totalSec + value*yearSec;
                default:      return Long.MAX_VALUE;
            }
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    public static String convertToString(long seconds) {
        long years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0;
        while (seconds > 0) {
            if(seconds >= yearSec)        { years++;    seconds -= yearSec;   }
            else if(seconds >= monthSec)  { months++;   seconds -= monthSec;  }
            else if(seconds >= weekSec)   { weeks++;    seconds -= weekSec;   }
            else if(seconds >= daySec)    { days++;     seconds -= daySec;    }
            else if(seconds >= hourSec)   { hours++;    seconds -= hourSec;   }
            else if(seconds >= minuteSec) { minutes++;  seconds -= minuteSec; }
            else break;
        }
        return (years > 0 ? years + " Year" + (years == 1 ? "":"s") + ", " : "") + (months > 0 ? months + " Month" + (months == 1 ? "":"s") + ", " : "") +
                (weeks > 0 ? weeks + " Week" + (weeks == 1 ? "":"s") + ", " : "") + (days > 0 ? days + " Day" + (days == 1 ? "":"s") + ", " : "") +
                (hours > 0 ? hours + " Hour" + (hours == 1 ? "":"s") + ", " : "") + (minutes > 0 ? minutes + " Minute" + (minutes == 1 ? "":"s") +
                (seconds == 0 ? "":", ") : "") + (seconds > 0 ? seconds + " Second" + (seconds == 1 ? "":"s") : "");
    }

    public static String getString() {
        update();

        int h = time.getHour(), m = time.getMinute(), s = time.getSecond();
        String h0 = "", m0 = "", s0 = "";
        if(h < 10) h0 = "0"; if(m < 10) m0 = "0"; if(s < 10) s0 = "0";

        return time.getDayOfMonth() + ". " + time.getMonthValue() + ". " + time.getYear() + " - " + h0+h + ":" + m0+m + ":" + s0+s;
    }
}