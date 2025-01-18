package com.outis.pwvault.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CryptoUtil {


    private final String secret;

    public CryptoUtil(@Value("${CRYPTO_SECRET}") String secret) {
        this.secret = secret;
    }

    private static byte[] sha256(String secret) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String encryptWithAES(String text) throws Exception {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        byte[] key = sha256(secret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        String ivHex = bytesToHex(iv);
        String encryptedText = Base64.getEncoder().encodeToString(encrypted);

        return ivHex + ":" + encryptedText;
    }

    public String decryptWithAES(String encrypted) throws Exception {
        String[] parts = encrypted.split(":");
        byte[] iv = hexToBytes(parts[0]);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        byte[] key = sha256(secret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encryptedPayload = Base64.getDecoder().decode(parts[1]);
        byte[] decrypted = cipher.doFinal(encryptedPayload);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}
