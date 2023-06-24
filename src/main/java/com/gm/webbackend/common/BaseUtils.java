package com.gm.webbackend.common;
import org.apache.commons.codec.binary.Base64;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BaseUtils {

    public static String encode(String tmp){

        return new String(Base64.encodeBase64(tmp.getBytes()));
    }


    public static String decode(byte[] tmp){
        return new String(Base64.decodeBase64(tmp));
    }

    public static Date transferString2Date(String s) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (ParseException e) {
            //LOGGER.error("时间转换错误, string = {}", s, e);
        }
        return date;
    }

    public static String transferDate2String(Date temp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(temp);
    }

}
