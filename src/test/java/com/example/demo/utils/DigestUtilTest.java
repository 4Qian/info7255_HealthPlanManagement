package com.example.demo.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DigestUtilTest {

    @Test
    void genEtag() {
        String source = "fsdfsdfsdfds3254lmdskflkl;zrlk421mg";
        String md5hex1 = DigestUtil.getEtag(source);
        String md5hex2 = DigestUtil.getEtag(source);
        assertEquals(md5hex1, md5hex2);
        System.out.println(md5hex1);
    }
}