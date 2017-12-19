package com.pack.jv.autocall;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by João on 12/04/2017.
 */

public class AES {

    public static String encrypt(String password, String cleartext)
            throws Exception {
        byte[] rawKey = getRawKey(password.toCharArray());
        IvParameterSpec iv = getIv(password.toCharArray());
        byte[] result = encrypt(rawKey, cleartext.getBytes(), iv);
        return toHex(result);
    }

    public static String decrypt(String password, String encrypted)
            throws Exception {
        byte[] rawKey = getRawKey(password.toCharArray());
        IvParameterSpec iv = getIv(password.toCharArray());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc, iv);
        return new String(result);
    }

    private static byte[] getRawKey(char[] password) throws Exception {
        String salt  = "H2Jksl20";
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec ks = new PBEKeySpec(password,salt.getBytes(),5000,128);
        SecretKey skey = f.generateSecret(ks);
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static IvParameterSpec getIv(char[] password) throws Exception{
        String salt = "AJEIOajakhw120";
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec ks = new PBEKeySpec(password, salt.getBytes(), 4768, 128);
        SecretKey skey = f.generateSecret(ks);
        byte[] rawIV = skey.getEncoded();
        IvParameterSpec iv = new IvParameterSpec(rawIV);
        return iv;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear, IvParameterSpec iv) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted, IvParameterSpec iv)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }


    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

}
