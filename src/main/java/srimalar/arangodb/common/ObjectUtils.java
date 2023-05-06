package srimalar.arangodb.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectUtils {
    private final static Logger logger = LoggerFactory.getLogger(ObjectUtils.class);
    private static final String EMAIL_REGEX = "^[\\w-+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";//"^[A-Za-z0-9+_.-]+@(.+)$"
    private static final String ALPHA_CHAR = "abcdefghijklmnopqrstuvxyz";
    private static MessageDigest messageDigest;

    private static MessageDigest getMessageDigest256() throws NoSuchAlgorithmException {
        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance("SHA-256");
        }
        return messageDigest;
    }

    private static SecretKey getSecretEncryptionKey(String keyText) {
        return new SecretKeySpec(getKey(keyText), "AES");
    }

    private static byte[] getKey(String keyStr) {
        byte[] key = null;
        try {
            key = (keyStr).getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = getMessageDigest256();
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public static byte[] hexToBytes(String content) {
        int len = content.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(content.charAt(i), 16) << 4)
                    + Character.digit(content.charAt(i+1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] byteArray) {
        StringBuilder builder = new StringBuilder();
        for (byte data : byteArray) {
            builder.append(String.format("%02x", data));
        }
        return builder.toString();
    }

    public static String getSHA256(String base) {
        try {
            byte[] hash = getMessageDigest256().digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String encryptHmacSha256(String key, String text) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            return ObjectUtils.encodeBase64(mac.doFinal(text.getBytes()));
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                ex.printStackTrace();
            } else {
                logger.error("ERROR : ENCRYPTION Hmac Sha256 : " + ex.getMessage());
            }
        }
        return null;
    }

    public static String encryptPKCS5Padding(String keyCode, String content) {
        if(keyCode != null && content != null) {
            try {
                SecretKey secKey = getSecretEncryptionKey(keyCode);
                Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
                return bytesToHex(aesCipher.doFinal(content.getBytes()));
            } catch (Exception ex) {
                // ignore Exception
            }
        }
        return null;
    }

    public static String decryptPKCS5Padding(String keyCode, String content) {
        if(keyCode != null && content != null) {
            try {
                byte[] contentBytes = hexToBytes(content);
                SecretKey secKey = getSecretEncryptionKey(keyCode);
                Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                aesCipher.init(Cipher.DECRYPT_MODE, secKey);
                byte[] bytePlainText = aesCipher.doFinal(contentBytes);
                return new String(bytePlainText, StandardCharsets.UTF_8);
            } catch (Exception ex) {
                // ignore Exception
            }
        }
        return null;
    }

    public static String decodeBase64(String value) {
        return new String(decodeBase64Byte(value), StandardCharsets.UTF_8);
    }

    public static byte[] decodeBase64Byte(String value) {
        return Base64.getDecoder().decode(value);
    }

    public static String decodeBase64(byte[] bytes) {
        return new String(Base64.getDecoder().decode(bytes), StandardCharsets.UTF_8);
    }

    public static String encodeBase64(String value) {
        return encodeBase64(value.getBytes());
    }

    public static String encodeBase64(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    public static String decodeURIComponent(String txt) {
        if (txt != null) {
            try {
                URLDecoder.decode(txt, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.debug(ex.getMessage());
            }
        }
        return null;
    }

    public static String encodeURIComponent(String txt) {
        String result;
        try {
            result = URLEncoder.encode(txt, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = txt;
        }
        return result;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (email != null && email.trim().length() > 4) {
            Pattern pattern = java.util.regex.Pattern.compile(EMAIL_REGEX);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
        return false;
    }

    public static boolean isEquals(String value1, String value2) {
        return value1 != null && value2 != null && value1.trim().equals(value2.trim());
    }

    public static String getUniqueId() {
        Random random = new Random();
        String tag = Long.toString(Math.abs(random.nextLong()), 36);
        return tag.substring(0, 6);
    }

    public static String generateKey() {
        return generateKey(8);
    }

    public static String generateKey(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (ALPHA_CHAR.length() * Math.random());
            sb.append(ALPHA_CHAR.charAt(index));
        }
        return sb.toString();
    }
}