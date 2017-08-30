package cn.ljj.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import cn.ljj.util.Logger;

public class CryptUtils {
    public static final String TAG = CryptUtils.class.getSimpleName();
    public static final String KEY_ALGORITHM = "RSA";
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final int KEY_SIZE = 2048;

    public static byte[] getFileString(File file) {
        if (file == null || !file.exists()) {
            Logger.e(TAG, "getFileString file=" + file);
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return buffer;
        } catch (Exception e) {
            Logger.e(TAG, "getFileString: " + file, e);
            e.printStackTrace();
        }
        return null;
    }

    public static KeyPair generateKeyPairs() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            Logger.e(TAG, "generateKeyPairs", e);
            e.printStackTrace();
        }
        return null;
    }

    public static boolean generateKeyPairsToPath(String folder) {
        KeyPair keyPair = generateKeyPairs();
        return saveKeyPairToFolder(keyPair, folder);
    }

    public static boolean saveKeyPairToFolder(KeyPair keyPair, String folder) {
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        File publicKeyFile = new File(folder, "rsa.pub");
        FileOutputStream publicKeyFileOutputStream = null;
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        File privateKeyFile = new File(folder, "rsa");
        FileOutputStream privateKeyFileOutputStream = null;
        try {
            publicKeyFileOutputStream = new FileOutputStream(publicKeyFile);
            publicKeyFileOutputStream.write(publicKeyBytes);
            privateKeyFileOutputStream = new FileOutputStream(privateKeyFile);
            privateKeyFileOutputStream.write(privateKeyBytes);
            return true;
        } catch (Exception e) {
            Logger.e(TAG, "saveKeyPairToFolder: folder=" + folder, e);
            e.printStackTrace();
            publicKeyFile.delete();
            privateKeyFile.delete();
        } finally {
            if (publicKeyFileOutputStream != null) {
                try {
                    publicKeyFileOutputStream.close();
                } catch (IOException e) {
                    Logger.e(TAG, "saveKeyPairToFolder: close public folder=" + folder, e);
                    e.printStackTrace();
                }
            }
            if (privateKeyFileOutputStream != null) {
                try {
                    privateKeyFileOutputStream.close();
                } catch (IOException e) {
                    Logger.e(TAG, "saveKeyPairToFolder: close private folder=" + folder, e);
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static byte[] symmetricalEncrypt(byte[] data, byte[] key) {
        byte[] temp = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            byte k = key[i % key.length];
            temp[i] = (byte) (data[i] ^ k);
        }
        return temp;
    }

    public static byte[] symmetricalDecrypt(byte[] data, byte[] key) {
        byte[] temp = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            byte k = key[i % key.length];
            temp[i] = (byte) (data[i] ^ k);
        }
        return temp;
    }
}
