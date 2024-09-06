package com.temx.workflow.util;

import com.temx.workflow.exception.FlowEndpointException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;

public class EncryptionUtils {

    private static final int TAG_LENGTH = 16;
    private static final String AES_ALGORITHM = "AES";
    private static final String GCM_ALGORITHM = "AES/GCM/NoPadding";
    private static final String RSA_ALGORITHM = "RSA";

    public static class DecryptedRequest {
        private final Map<String, Object> decryptedBody;
        private final byte[] aesKeyBuffer;
        private final byte[] initialVectorBuffer;

        public DecryptedRequest(Map<String, Object> decryptedBody, byte[] aesKeyBuffer, byte[] initialVectorBuffer) {
            this.decryptedBody = decryptedBody;
            this.aesKeyBuffer = aesKeyBuffer;
            this.initialVectorBuffer = initialVectorBuffer;
        }

        public Map<String, Object> getDecryptedBody() {
            return decryptedBody;
        }

        public byte[] getAesKeyBuffer() {
            return aesKeyBuffer;
        }

        public byte[] getInitialVectorBuffer() {
            return initialVectorBuffer;
        }
    }

    public static DecryptedRequest decryptRequest(Map<String, String> body, PrivateKey privateKey, String passphrase) {
        String encryptedAesKey = body.get("encrypted_aes_key");
        String encryptedFlowData = body.get("encrypted_flow_data");
        String initialVector = body.get("initial_vector");

        byte[] decryptedAesKey;
        try {
            // Decrypt AES key created by client
            decryptedAesKey = decryptAesKey(encryptedAesKey, privateKey);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new FlowEndpointException(421, "Failed to decrypt the request. Please verify your private key.");
        }

        // Decrypt flow data
        byte[] flowDataBuffer = Base64.getDecoder().decode(encryptedFlowData);
        byte[] initialVectorBuffer = Base64.getDecoder().decode(initialVector);

        byte[] encryptedFlowDataBody = new byte[flowDataBuffer.length - TAG_LENGTH];
        byte[] encryptedFlowDataTag = new byte[TAG_LENGTH];

        System.arraycopy(flowDataBuffer, 0, encryptedFlowDataBody, 0, encryptedFlowDataBody.length);
        System.arraycopy(flowDataBuffer, encryptedFlowDataBody.length, encryptedFlowDataTag, 0, encryptedFlowDataTag.length);

        String decryptedJSONString = decryptFlowData(decryptedAesKey, initialVectorBuffer, encryptedFlowDataBody, encryptedFlowDataTag);

        return new DecryptedRequest(Map.of("decryptedBody", decryptedJSONString), decryptedAesKey, initialVectorBuffer);
    }

    private static byte[] decryptAesKey(String encryptedAesKey, PrivateKey privateKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(Base64.getDecoder().decode(encryptedAesKey));
    }

    private static String decryptFlowData(byte[] aesKey, byte[] iv, byte[] encryptedData, byte[] tag) {
        try {
            Cipher cipher = Cipher.getInstance(GCM_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, AES_ALGORITHM), spec);
            cipher.updateAAD(tag);
            byte[] decrypted = cipher.doFinal(encryptedData);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt flow data", e);
        }
    }

    public static String encryptResponse(String response, byte[] aesKeyBuffer, byte[] initialVectorBuffer) {
        // Flip initial vector
        byte[] flippedIv = new byte[initialVectorBuffer.length];
        for (int i = 0; i < initialVectorBuffer.length; i++) {
            flippedIv[i] = (byte) ~initialVectorBuffer[i];
        }

        // Encrypt response data
        try {
            Cipher cipher = Cipher.getInstance(GCM_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH * 8, flippedIv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKeyBuffer, AES_ALGORITHM), spec);

            byte[] encryptedData = cipher.doFinal(response.getBytes(StandardCharsets.UTF_8));
            byte[] authTag = cipher.getIV();

            byte[] combinedResult = new byte[encryptedData.length + authTag.length];
            System.arraycopy(encryptedData, 0, combinedResult, 0, encryptedData.length);
            System.arraycopy(authTag, 0, combinedResult, encryptedData.length, authTag.length);

            return Base64.getEncoder().encodeToString(combinedResult);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt response", e);
        }
    }
}

