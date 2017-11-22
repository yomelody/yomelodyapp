package com.yomelody.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ajay on 7/11/2017.
 */

public class RMethod {
    public static String getServerDiffrenceDate(String ServerDate)
    {
        String clientDnT = ServerDate ;// "2017-06-01 07:20:00";
        Date date2=null;
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String val = "";
        try{
            dff.setTimeZone(TimeZone.getTimeZone("UTC"));
            date2 = dff.parse(clientDnT);
            dff.setTimeZone(TimeZone.getDefault());
            String formattedDate = dff.format(date2);


            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();

            long diff = currentDate.getTime() - date2.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long years=(days/365);
            if (days == 0) {
                if (hours == 0) {
                    if (minutes == 0) {
                        if (seconds <= 5) {
                            val = "Just now";
                        } else {
                            val = String.valueOf(seconds) + " " + "secs"+" ago";
                        }
                    } else if (minutes == 1) {
                        val = String.valueOf(minutes) + " " + "min" +" ago";
                    } else {
                        val = String.valueOf(minutes) + " " + "mins"+" ago";
                    }
                } else if (hours == 1) {
                    val = String.valueOf(hours) + " " + "hour"+" ago";
                } else {
                    val = String.valueOf(hours) + " " + "hrs"+" ago";
                }
            } else if (days == 1) {
                val = "1 day";
            }
            else {
                long year=(days/365);
                if(year>0) {
                    val = String.valueOf(days / 365) + "year" + " ago";
                }
                else {
                    val = days + "day" + " ago";
                }
            }


        }
        catch(Exception e){
            System.err.println(e);
        }

        return val;
    }


    public static void printDifference(String dateTime){

        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date currentDate = Calendar.getInstance().getTime();

        try {
            dff.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date2 = dff.parse(dateTime);

            //milliseconds
            long different = currentDate.getTime() - date2.getTime();

            System.out.println("startDate : " + currentDate.getTime());
            System.out.println("endDate : "+ date2);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays,
                    elapsedHours, elapsedMinutes, elapsedSeconds);

        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

}


/*
* SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
Date date = simpleDateFormat.parse("2013-06-19 00:13:21");

TimeZone destTz = TimeZone.getTimeZone("yourtimezone");
simpleDateFormat.setTimeZone(destTz);
String result = simpleDateFormat.format(date);*/