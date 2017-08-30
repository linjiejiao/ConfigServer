package cn.ljj.crypt;

import java.io.File;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import cn.ljj.util.Logger;

public class Decryptor {
    private static final String TAG = Decryptor.class.getSimpleName();
    protected PrivateKey mPrivateKey;

    public Decryptor(byte[] privateKey) throws Exception {
        if (privateKey == null || privateKey.length <= 0) {
            throw new IllegalArgumentException("pubKey can not be empty!");
        }
        generatePrivateKey(privateKey);
    }

    public Decryptor(File rivateKeyFile) throws Exception {
        this(CryptUtils.getFileString(rivateKeyFile));
    }

    protected void generatePrivateKey(byte[] privateKey) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory factory = KeyFactory.getInstance(CryptUtils.KEY_ALGORITHM);
        mPrivateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
    }

    public byte[] RSADecrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(CryptUtils.CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, mPrivateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            Logger.e(TAG, "RSADecrypt: " + Base64.getEncoder().encodeToString(data), e);
            e.printStackTrace();
        }
        return null;
    }
}
