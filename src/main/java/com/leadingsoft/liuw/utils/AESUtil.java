package com.leadingsoft.liuw.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lbt on 16/6/21.
 */
@Slf4j
public class AESUtil {


    public static final String CBC_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String KEY_ALGORITHM = "AES";

    public static String aesCbcDecode(String encryptedData, String sessionKey, String iv) {
        SecretKey secretKey = new SecretKeySpec(Base64.decodeBase64(sessionKey), KEY_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decodeBase64(iv));

        try {
            Cipher cipher = Cipher.getInstance(CBC_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return new String(cipher.doFinal(Base64.decodeBase64(encryptedData)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException e) {

            log.warn("解密失败", e);
            return null;
        }
    }
}
