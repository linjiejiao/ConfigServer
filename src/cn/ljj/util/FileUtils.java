package cn.ljj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static String getFileString(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            return getStringFromStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "getFileString: " + path, e);
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringFromStream(InputStream inputStream) {
        String str = "";
        try {
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            str = new String(buffer);
        } catch (IOException e) {
            Logger.e(TAG, "getStringFromStream: " + inputStream, e);
            e.printStackTrace();
            return null;
        }
        return str;
    }

    public static Map<String, String> getConfigs(String configFile) {
        File file = new File(configFile);
        HashMap<String, String> configs = new HashMap<String, String>();
        if (file.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    line = line.trim();
                    if (line.length() <= 0 || line.startsWith("#") || !line.contains("=")) {
                        continue;
                    }
                    int index = line.indexOf("=");
                    String key = line.substring(0, index);
                    String value = line.substring(index + 1);
                    configs.put(key, value);
                }
            } catch (Exception e) {
                Logger.e(TAG, "getConfigs: " + configFile, e);
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Logger.e(TAG, "getConfigs close: " + configFile, e);
                        e.printStackTrace();
                    }
                }
            }
        }
        return configs;
    }

    public static void saveConfigTofile(Map<String, String> config, String configFile) {
        File file = new File(configFile);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            Set<String> keys = config.keySet();
            for (String key : keys) {
                String value = config.get(key);
                if (value != null) {
                    writer.write(key + "=" + value + "\n");
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "saveConfigTofile: " + configFile, e);
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Logger.e(TAG, "saveConfigTofile close: " + configFile, e);
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getConflictResovedPath(String folder, String fileName) {
        File file = new File(folder, fileName);
        int i = 0;
        int index = fileName.lastIndexOf(".");
        String tempFileName;
        while (file.exists() && file.isFile()) {
            i++;
            if (index != -1) {
                String name = fileName.substring(0, index);
                String extension = fileName.substring(index + 1);
                tempFileName = name + "(" + i + ")." + extension;
            } else {
                tempFileName = fileName + "(" + i + ")";
            }
            file = new File(folder, tempFileName);
        }
        return file.getPath();
    }
}
