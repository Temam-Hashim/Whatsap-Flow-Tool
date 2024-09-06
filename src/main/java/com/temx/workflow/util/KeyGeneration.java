package com.temx.workflow.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

public class KeyGeneration {

    public static void main(String[] args) throws Exception {
        // Generate AES Key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // 256-bit AES
        SecretKey secretKey = keyGenerator.generateKey();
        String base64AesKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        // Generate IV
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16]; // 128-bit IV
        secureRandom.nextBytes(iv);
        String base64Iv = Base64.getEncoder().encodeToString(iv);

        System.out.println("AES Key: " + base64AesKey);
        System.out.println("IV: " + base64Iv);
    }
}
