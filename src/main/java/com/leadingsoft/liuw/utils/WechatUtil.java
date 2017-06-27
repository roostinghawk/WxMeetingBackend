package com.leadingsoft.liuw.utils;

import java.security.MessageDigest;
import java.util.Arrays;

public class WechatUtil {

    /**
     * 生成签名
     * 
     * @param token
     * @param timestamp
     * @param nonce
     * @return
     */
    public static String generateSignature(String token, String timestamp, String nonce) {
        String result = "";

        try {
            final String[] array = new String[] {token, timestamp, nonce };
            final StringBuffer sb = new StringBuffer();
            Arrays.sort(array);
            for (int i = 0; i < 3; i++) {
                sb.append(array[i]);
            }
            final String str = sb.toString();
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            final byte[] digest = md.digest();

            final StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (final byte element : digest) {
                shaHex = Integer.toHexString(element & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            result = hexstr.toString();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
