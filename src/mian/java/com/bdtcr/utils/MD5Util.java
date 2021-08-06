package com.bdtcr.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
  * @Author sunweisong
  * @Date 2019/9/19 8:22 PM
  */
public class MD5Util {

    /**
      * generate MD5 hash value for a string.
      * @param string
      * @return String
      * @throws RuntimeException
      * @date 2019/9/19 8:28 PM
      * @author sunweisong
      */
    public static String getMD5(String string) {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // C# has different encoding format from Java
            // Use "UTF-8" to unit encoding format.
            messageDigest.update(string.getBytes("UTF-8"));
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] digestArray = messageDigest.digest(string.getBytes());
            // Convert byte array into signum representation
            BigInteger bigInteger = new BigInteger(1, digestArray);
            // Convert message digest into hex value
            String hashText = bigInteger.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
