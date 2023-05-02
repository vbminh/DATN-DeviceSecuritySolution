package algorithm;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

public class DES {
    private String SECRET_KEY = "15032001";

    public String Encrypt(String policy) {
        try {
            // biểu diễn khóa k dưới dạng byte
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = SECRET_KEY.getBytes("UTF-8");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 8);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "DES");

            /*
             * DES/ECB/PKCS5PADDING DES(Data Encryption Standard): tên thuật toán
             * ECB(Electronic Codebook mode)(tùy chọn): mã hóa từng khối bit độc lập
             * PKCS5Padding(Padding scheme: tùy chọn): độ dài dữ liệu đầu vào tùy ý, bản mã
             * sẽ được đệm thành bội của 8byte
             */
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5PADDING");// mã hóa khóa k 56bit
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] byteEncrypted = cipher.doFinal(policy.getBytes());// Hoàn thành thao tác mã hóa
            String encrypted = Base64.getEncoder().encodeToString(byteEncrypted);// chuyển thành bản mã
            return encrypted;
        } catch (Exception e) {
            return null;
        }
    }

    public String Decrypt(String cipherPolicy) {
        try {
            // biểu diễn khóa k dưới dạng byte
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = SECRET_KEY.getBytes("UTF-8");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 8);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "DES");

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] byteDecrypted = cipher.doFinal(Base64.getDecoder().decode(cipherPolicy));
            String decrypted = new String(byteDecrypted);
            return decrypted;
        } catch (Exception e) {
            return null;
        }
    }
}
