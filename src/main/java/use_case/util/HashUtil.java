package use_case.util;

import use_case.port.outgoing.PasswordHashingPort;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {


    public static String hash(String rawPassword) throws IllegalArgumentException, NoSuchAlgorithmException {
        if(rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("input can't be null");
        }

        // compute hash with SHA-1
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = sha.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    static public boolean matches(String rawPassword, String hashedPassword) {
        try {
            if ( rawPassword != null && !rawPassword.isEmpty() && hashedPassword.equals(hash(rawPassword))) {
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }
}
