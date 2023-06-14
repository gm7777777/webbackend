package com.gm.webbackend.common;
import org.apache.commons.codec.binary.Base64;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class BaseUtils {

    public static String encode(String tmp){

        return String .valueOf(Base64.encodeBase64(tmp.getBytes()));
    }


    public static String decode(byte[] tmp){
        return String.valueOf(Base64.decodeBase64(tmp));
    }
}
