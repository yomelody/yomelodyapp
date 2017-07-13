package com.instamelody.instamelody.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ajay on 7/11/2017.
 */

public class RMethod {
    public static Date getServerDiffrenceDate(String ServerDate)
    {
        String clientDnT = ServerDate ;// "2017-06-01 07:20:00";
        Date date2=null;
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try{
            dff.setTimeZone(TimeZone.getTimeZone("UTC"));
            date2 = dff.parse(clientDnT);
            dff.setTimeZone(TimeZone.getDefault());
            String formattedDate = dff.format(date2);
            String ts4;
        }
        catch(Exception e){
            System.err.println(e);
        }

        return date2;
    }
}
