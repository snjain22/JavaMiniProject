package com.pwdmgr.passwordmanagerv1;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.security.*;
import java.util.Base64;


public class SecretKeyManipulation {//

    // Get the .key file for a given alias
    public static File getAESKeyFile(String directoryPath, String alias) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return new File(directory, alias + ".key");
    }

    // Load an AES key from a .key file
    public static SecretKey loadAESKeyFromFile(File keyFile) throws Exception {
        FileInputStream fis = new FileInputStream(keyFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        SecretKey secretKey = (SecretKey) ois.readObject();
        ois.close();
        fis.close();
        return secretKey;
    }

    // Save an AES key to a .key file
    public static void saveAESKeyToFile(File keyFile, SecretKey secretKey) throws Exception {
        FileOutputStream fos = new FileOutputStream(keyFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(secretKey);
        oos.close();
        fos.close();
    }

    // Generate a new AES key
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // You can also use 256 for a 256-bit key
        return keyGenerator.generateKey();
    }

    // Encrypt the password using AES/GCM/NoPadding
    public static String encryptPassword(String password, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(password.getBytes());
        byte[] iv = cipher.getIV();
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // Decrypt the password using the key
    public static String decryptPassword(String encryptedPassword, SecretKey secretKey) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedPassword);
        byte[] iv = new byte[12]; // IV length for AES/GCM/NoPadding
        byte[] encryptedBytes = new byte[combined.length - iv.length];

        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }
}
