package com.temx.workflow.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    public static PrivateKey getPrivateKeyFromPem(String passphrase) throws Exception {
        // Load the private key from the PEM file in resources
        ClassPathResource resource = new ClassPathResource("private-key.pem");
        InputStream inputStream = resource.getInputStream();
        String pem = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        String privateKeyPEM = pem
                .replace("-----BEGIN ENCRYPTED PRIVATE KEY-----", "")
                .replace("-----END ENCRYPTED PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        return keyFactory.generatePrivate(keySpec);
    }
}
