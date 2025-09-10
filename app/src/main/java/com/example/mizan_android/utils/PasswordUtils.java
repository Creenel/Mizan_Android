package com.example.mizan_android.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import android.util.Base64; // For encoding

public class PasswordUtils {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    // Generate salt, hashes password, returns "salt:hash"
    public static String generatePasswordStorage(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt); // Add salt
            byte[] hashedPasswordBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            String encodedSalt = Base64.encodeToString(salt, Base64.NO_WRAP);
            String encodedHash = Base64.encodeToString(hashedPasswordBytes, Base64.NO_WRAP);

            return encodedSalt + ":" + encodedHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Verifies password against "salt:hash" string
    public static boolean verifyPassword(String inputPassword, String storedPasswordStorage) {
        if (storedPasswordStorage == null || !storedPasswordStorage.contains(":")) {
            return false;
        }
        String[] parts = storedPasswordStorage.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
            String storedHash = parts[1];

            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] inputPasswordHashedBytes = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));
            String inputPasswordEncodedHash = Base64.encodeToString(inputPasswordHashedBytes, Base64.NO_WRAP);

            return inputPasswordEncodedHash.equals(storedHash);
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}