package cn.ljj.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UrlStringUtil {
    private static final String TAG = UrlStringUtil.class.getSimpleName();

    public static String urlStringByAppendingParameters(String url, Map<String, String> parameter) {
        if (url != null && parameter != null && parameter.size() > 0) {
            StringBuilder sb = new StringBuilder(url);
            if (!url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            for (String key : parameter.keySet()) {
                try {
                    String value = URLEncoder.encode(parameter.get(key), "UTF-8");
                    key = URLEncoder.encode(key, "UTF-8");
                    sb.append(key).append("=").append(value).append("&");
                } catch (UnsupportedEncodingException e) {
                    Logger.e(TAG, "urlStringByAppendingParameters: " + url + ", parameter:" + parameter, e);
                    e.printStackTrace();
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            url = sb.toString();
        }
        return url;
    }

    public static Map<String, String> parseQueryString(String queryString) {
        return parseQueryString(queryString, true);
    }

    public static Map<String, String> parseQueryString(String queryString, boolean urlDecode) {
        HashMap<String, String> parameters = new HashMap<>();
        if (queryString != null) {
            if (urlDecode) {
                try {
                    queryString = URLDecoder.decode(queryString, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Logger.e(TAG, "parseQueryString: " + queryString, e);
                    e.printStackTrace();
                }
            }
            if (queryString.startsWith("?")) {
                queryString = queryString.substring(1);
            }
            String[] parameterParts = queryString.split("&");
            if (parameterParts != null && parameterParts.length > 0) {
                for (String p : parameterParts) {
                    String[] pair = p.split("=");
                    if (pair != null && pair.length >= 2) {
                        parameters.put(pair[0], pair[1]);
                    }
                }
            }
        }
        return parameters;
    }

    public static String buildUrl(String scheme, String domain, String path, Map<String, String> parameter) {
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append("://").append(domain).append("/").append(path);
        return urlStringByAppendingParameters(sb.toString(), parameter);
    }
}
