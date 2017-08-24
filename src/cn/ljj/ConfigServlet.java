package cn.ljj;

import java.io.File;
import java.io.IOException;
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
    public static final String KEY_PARAM_TOKEN = "token";
    public static final String KEY_PARAM_QUERY_KEYS = "query_keys";
    public static final String AUTH_TOKEN_FILE_NAME = "auth_token";
    public static final String KEY_CONFIG_PARAM = "config_param";

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
        String file = getInitParameter("log4j-init-file");
        if (file != null) {
            PropertyConfigurator.configure(prefix + file);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     *      http://localhost:8080/ConfigServer?token=xxxx&query_keys=key1,key2,key3
     *
     *      respond:{key1=value1,key2=value2,key3=value3}
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.dumpRequest(request);
        if (!checkAuthorizion(request, response)) {
            Logger.e(TAG, "doGet Authorize failed!");
            return;
        }
        String queryKeys = UrlStringUtil.parseQueryString(request.getQueryString()).get(KEY_PARAM_QUERY_KEYS);
        if (queryKeys != null && queryKeys.length() > 0) {
            String[] keys = queryKeys.split(",");
            JsonObject object = new JsonObject();
            for (String key : keys) {
                String value = Config.getInstance().get(key);
                if (value != null) {
                    object.addProperty(key, value);
                }
            }
            response.getWriter().append(object.toString());
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response) http://localhost:8080/ConfigServer?token=xxxx
     *      parameter:{key1=value1,key2=value2,key3=value3}
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.dumpRequest(request);
        if (!checkAuthorizion(request, response)) {
            Logger.e(TAG, "doPost Authorize failed!");
            return;
        }
        JsonParser parser = new JsonParser();
        String config = request.getParameter(KEY_CONFIG_PARAM);
        JsonElement element = parser.parse(config);
        JsonObject object = element.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = object.entrySet();
        for (Entry<String, JsonElement> entry : entrySet) {
            Config.getInstance().set(entry.getKey(), entry.getValue().getAsString());
        }
        response.getWriter().write(entrySet.size());
    }

    private boolean checkAuthorizion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = UrlStringUtil.parseQueryString(request.getQueryString()).get(KEY_PARAM_TOKEN);
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
}
