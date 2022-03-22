package com.example.demo.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class DigestUtil {

    public static String getEtag(String payload) {
        return DigestUtils.md5Hex(payload);
    }

    public static String getEtagQuoted(String payload) {
        return "\"" + DigestUtils.md5Hex(payload) + "\"";
    }
}
