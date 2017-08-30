package cn.ljj;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.PropertyConfigurator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.ljj.crypt.CryptUtils;
import cn.ljj.crypt.Decryptor;
import cn.ljj.util.Config;
import cn.ljj.util.FileUtils;
import cn.ljj.util.Logger;
import cn.ljj.util.ServletUtil;
import cn.ljj.util.UrlStringUtil;

/**
 * Servlet implementation class ConfigServlet
 */
@WebServlet("/ConfigServlet")
public class ConfigServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String TAG = ConfigServlet.class.getSimpleName();
    public static final String KEY_PARAM = "param"; // encrypted request parameters
    public static final String KEY_PARAM_TOKEN = "token"; // request token
    public static final String KEY_PARAM_CRYPT_KEY = "crypt_key"; // respond encrypt key
    public static final String KEY_PARAM_QUERY_KEYS = "query_keys";
    public static final String AUTH_TOKEN_FILE_NAME = "auth_token";
    public static final String KEY_CONFIG_PARAM = "config_param";

    private Decryptor mDecryptor = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfigServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        String prefix = getServletContext().getRealPath("/");
        String log4jConfigFile = getInitParameter("log4j-init-file");
        if (log4jConfigFile != null) {
             PropertyConfigurator.configure(prefix + log4jConfigFile);
        }
        String rsaFile = getInitParameter("rsa-file");
        try {
             mDecryptor = new Decryptor(new File(prefix + rsaFile));
        } catch (Exception e) {
            Logger.e(TAG, "init mDecryptor: " + prefix + rsaFile, e);
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) http://localhost:8080/ConfigServer?param=qwertyuiop
     *
     *      qwertyuiop decrypt -> token=xxxx&query_keys=key1,key2,key3&crypt_key=xxxx
     *
     *      {key1=value1,key2=value2,key3=value3} encrypt(crypt_key) -> asdfghjkl
     *
     *      respond: asdfghjkl
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.dumpRequest(request);
        String encryptedParameters = UrlStringUtil.parseQueryString(request.getQueryString(), false).get(KEY_PARAM);
        String decryptedQueryString = decrypt(encryptedParameters);
        if (!checkAuthorizion(decryptedQueryString, response)) {
            Logger.e(TAG, "doGet Authorize failed!");
            return;
        }
        Map<String, String> map = UrlStringUtil.parseQueryString(decryptedQueryString);
        String queryKeys = UrlStringUtil.parseQueryString(decryptedQueryString).get(KEY_PARAM_QUERY_KEYS);
        if (queryKeys != null && queryKeys.length() > 0) {
            String[] keys = queryKeys.split(",");
            JsonObject object = new JsonObject();
            for (String key : keys) {
                String value = Config.getInstance().get(key);
                if (value != null) {
                    object.addProperty(key, value);
                }
            }
            String encryptedRespond = encrypt(object.toString(), map.get(KEY_PARAM_CRYPT_KEY));
            response.getWriter().append(encryptedRespond);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) http://localhost:8080/ConfigServer?param=qwertyuiop post
     *
     *      qwertyuiop decrypt -> token=xxxx
     *
     *      parameter: config_param: asdfghjkl
     *
     *      asdfghjkl decrypt -> {key1=value1,key2=value2,key3=value3}
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.dumpRequest(request);
        String encryptedParameters = UrlStringUtil.parseQueryString(request.getQueryString(), false).get(KEY_PARAM);
        String decryptedQueryString = decrypt(encryptedParameters);
        if (!checkAuthorizion(decryptedQueryString, response)) {
            Logger.e(TAG, "doPost Authorize failed!");
            return;
        }
        JsonParser parser = new JsonParser();
        String config = decrypt(request.getParameter(KEY_CONFIG_PARAM));
        JsonElement element = parser.parse(config);
        JsonObject object = element.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = object.entrySet();
        for (Entry<String, JsonElement> entry : entrySet) {
            Config.getInstance().set(entry.getKey(), entry.getValue().getAsString());
        }
        response.getWriter().write("" + entrySet.size());
    }

    private boolean checkAuthorizion(String queryString, HttpServletResponse response) throws IOException {
        String token = UrlStringUtil.parseQueryString(queryString).get(KEY_PARAM_TOKEN);
        String authTokenPath = Config.userHomePath() + File.separator + AUTH_TOKEN_FILE_NAME;
        String configToken = FileUtils.getFileString(authTokenPath);
        if (configToken == null) {
            Logger.i(TAG, "checkAuthorizion configToken not set!");
            return false;
        }
        if (!configToken.equals(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    /**
     * base64 -> bin -> decrypt -> string
     *
     * @param base64String
     * @return
     */
    private String decrypt(String base64String) {
        if (base64String == null || base64String.length() == 0) {
            Logger.e(TAG, "decrypt failed! input base64String = " + base64String);
            return "";
        }
        byte[] data = Base64.getDecoder().decode(base64String);
        if (mDecryptor != null) {
            byte[] decrypedData = mDecryptor.RSADecrypt(data);
            if (decrypedData != null) {
                return new String(decrypedData);
            } else {
                Logger.e(TAG, "decrypt failed! decrypedData = null!");
                return base64String;
            }
        } else {
            Logger.e(TAG, "decrypt failed! mDecryptor = null!");
            return base64String;
        }
    }

    /**
     * symmetric encrypt base64
     *
     * @param original
     * @param cryptKey
     * @return
     */
    private String encrypt(String original, String cryptKey) {
        if (original == null || original.length() == 0) {
            Logger.e(TAG, "encrypt failed! original = " + original);
            return "";
        }
        if (cryptKey == null || cryptKey.length() == 0) {
            Logger.e(TAG, "encrypt failed! cryptKey = " + cryptKey);
            return original;
        }
        byte[] cryptedData;
        cryptedData = CryptUtils.symmetricalEncrypt(original.getBytes(), cryptKey.getBytes());
        if (cryptedData == null) {
            Logger.e(TAG, "encrypt failed! cryptedData = null!");
            return "";
        }
        return Base64.getEncoder().encodeToString(cryptedData);
    }
}
