package com.example.app_schedule;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class Tool {

    public static String getAMOrPM(int hour) {
        if(hour == 24 ||hour <= 12) {
            return "AM";
        } else {
            return "PM";
        }
    }

    public static int minusDayFromTheNow(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYearOfNow(), getMonthOfNow() - 1, getDayOfNow());
        calendar.add(Calendar.DATE, -amount);
      SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(dateFormatter.format(calendar.getTime()));
    }

    public static int concatenateDateFormat(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(dateFormatter.format(calendar.getTime()));
    }


    public static long concatenateDayInfoIdPlanId(int first, int second) {
        String string1 = Integer.toString(first);
        String string2;
        if(second < 10) {
            string2 ="0".concat(Integer.toString(second));
        } else {
            string2 = Integer.toString(second);
        }
        String concatenated = string1.concat(string2);
        return Long.parseLong(concatenated);
    }

    public static String add0SmallerThan10(int i) {
        if(i < 10) {
            return "0".concat(Integer.toString(i));
        } else {
            return Integer.toString(i);
        }
    }

    public static boolean isLeapYear(int year) {
        return (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0));
    }
    public static int getNow() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(mFormat.format(mDate));
    }


    public static int getYearOfNow() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy");
        return Integer.parseInt(mFormat.format(mDate));
    }

    public static int getMonthOfNow() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("MM");
        return Integer.parseInt(mFormat.format(mDate));
    }

    public static int getDayOfNow() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("dd");
        return Integer.parseInt(mFormat.format(mDate));
    }

    public static String getMonthsEnglishName(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return null;
        }
    }

    /**
     * ?????? ????????? ????????? ????????? ??????(??? ~ ???)
     * @param date
     * @return
     * @throws Exception
     */
    public static String DateToDayOfWeek(String date) throws Exception {

        String day = "";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        Date nDate = dateFormat.parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "???";
                break;
            case 2:
                day = "???";
                break;
            case 3:
                day = "???";
                break;
            case 4:
                day = "???";
                break;
            case 5:
                day = "???";
                break;
            case 6:
                day = "???";
                break;
            case 7:
                day = "???";
                break;

        }
        return day;
    }

    /**
     * 0 ~ 360 ????????? ?????? ????????? ???????????? example: 90??? = 6???, 270??? 18???
     * @param angle ??????
     * @return
     */
    public static int angleToHour(double angle) {
        return (int) ((angle * (double) 4) / 60);
    }

    /**
     * 0 ~ 360 ????????? ???????????? ????????? ??? ????????? ????????????. example: 93??? = 12???
     * @param angle ??????
     * @return
     */
    public static int angleToMinute(double angle) {
        return (int) ((angle * (double) 4) % 60);
    }

    /**
     * 24???????????? ?????????????????? ????????? 12???????????? ???????????? 23??? -> 11???
     * @param hourOf24 24?????? ??????
     * @return 12?????? ??????
     */
    public static int toHourOf12 (int hourOf24 ) {
        if(hourOf24 == 24 || hourOf24 == 0 || hourOf24 == 12) {
            return 12;
        } else if(hourOf24 <= 11) {
            return hourOf24;
        } else {
            return hourOf24 - 12;
        }
    }

    public static double timeToAngle(int hour, int minute) {
        return ((double) ((hour * 60) + minute) / (double) 4);
    }

    /**
     * -180 ~ 180 ??? ????????? ??????????????? ???????????? 12?????? 0 ~ 360?????? ??????
     * @param angle -180 ~ 180 ??? ??????
     * @return
     */
    public static double pieTo360Angle(double angle) {
        if(angle >= 0) {
            return angle;
        } else {
            return ((double) 360) + angle;
        }
    }
}
