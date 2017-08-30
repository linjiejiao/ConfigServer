package cn.ljj.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Config {
    static Config sInstance = null;
    private Map<String, String> configMap = new HashMap<String, String>();

    public static Config getInstance() {
        if (sInstance == null) {
            sInstance = new Config();
            sInstance.loadFromFile(getConfigPath(), false);
        }
        return sInstance;
    }

    private static String getConfigPath() {
        return userHomePath() + File.separator + "config.ini";
    }

    public static String userHomePath() {
        String home = System.getProperty("user.home");
        if ("/var/root".equals(home)) { // root user
            home = "/Users";
        }
        return home;
    }

    private Config() {

    }

    public void loadFromFile(String configFile, boolean clean) {
        if (clean) {
            configMap.clear();
        }
        configMap.putAll(FileUtils.getConfigs(configFile));
    }

    public String get(String key) {
        return configMap.get(key);
    }

    public void set(String key, String value) {
        configMap.put(key, value);
        Map<String, String> config = FileUtils.getConfigs(getConfigPath());
        config.put(key, value);
        FileUtils.saveConfigTofile(config, getConfigPath());
    }

}
