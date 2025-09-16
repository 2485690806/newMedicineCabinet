package leesche.smartrecycling.base.utils;

import android.util.Base64;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtilAes256 {

    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    public static int PBKDF2_ITERATION_COUNT = 501;
    private static final int PBKDF2_KEY_LENGTH = 256;
    private static final int SECURE_IV_LENGTH = 64;
    private static final int SECURE_KEY_LENGTH = 128;
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String PBKDF2_SALT = "hY0wTq6xwc6ni01G";
    private static final Random RANDOM = new SecureRandom();

    // Reference: https://github.com/Ideas2IT/cordova-aes256/blob/master/src/android/AES256.java

    public static String secureKey = "5F012E9E714F875505CFFE126D2ECE13";
    public static String iv = "418FBE72CB960327";

    /**
     * @param args
     * @throws Exception
     */
    public static void test() throws Exception {

        // Change Following to test
        final String password = "xuLhe~A]XU-;3Y/vT4~jtcbnM]<u9E";
        //final String testData = "testdata";
        final String testData = "123456-1546314022768";
        System.out.println("Test Data: " + testData);

        secureKey = generateSecureKey(password);
        iv = generateSecureIV(password);

        String encrypted = encrypt(testData);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Len:" + encrypted.length());

        encrypted = "U2FsdGVkX1+fNzzsMoAbXbInHlaSBXU5+3KMyUHbgw89fIjfDRd7Y0PKZCts93ll";
        String decrypted = decrypt(encrypted);

        System.out.println("Decrypted: " + decrypted);

    }

    public static String encrypt(String value) throws Exception {
        byte[] pbkdf2SecuredKey = generatePBKDF2(secureKey.toCharArray(), PBKDF2_SALT.getBytes("UTF-8"),
                PBKDF2_ITERATION_COUNT, PBKDF2_KEY_LENGTH);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(pbkdf2SecuredKey, "AES");

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(value.getBytes());

        return Base64.encodeToString(encrypted, Base64.DEFAULT);

    }


    public static String decryptNullWhenException(String value) {
        try {
            return decrypt(value);
        } catch(Exception e) {
            return null;
        }
    }

    public static String decrypt(String value) throws Exception {
        byte[] pbkdf2SecuredKey = generatePBKDF2(secureKey.toCharArray(), PBKDF2_SALT.getBytes("UTF-8"),
                PBKDF2_ITERATION_COUNT, PBKDF2_KEY_LENGTH);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(pbkdf2SecuredKey, "AES");
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] original = cipher.doFinal(Base64.decode(value, Base64.DEFAULT));
        return new String(original);
    }

    private static byte[] generatePBKDF2(char[] password, byte[] salt, int iterationCount, int keyLength)
            throws Exception {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        KeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey.getEncoded();
    }

    private static String generateSecureKey(String password) throws Exception {
        byte[] secureKeyInBytes = generatePBKDF2(password.toCharArray(), generateRandomSalt(), PBKDF2_ITERATION_COUNT,
                SECURE_KEY_LENGTH);
        return printHexBinary(secureKeyInBytes);
    }

    /**
     * <p>
     * This method used to generate the secure IV based on the PBKDF2 algorithm
     * </p>
     *
     * @param password
     *            The password
     * @return SecureIV
     * @throws Exception
     */
    public static String generateSecureIV(String password) throws Exception {
        byte[] secureIVInBytes = generatePBKDF2(password.toCharArray(), generateRandomSalt(), PBKDF2_ITERATION_COUNT,
                SECURE_IV_LENGTH);
        return printHexBinary(secureIVInBytes);
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static byte[] generateRandomSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

}