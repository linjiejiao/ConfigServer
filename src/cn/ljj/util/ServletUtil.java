package cn.ljj.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class ServletUtil {
	private static final String TAG = ServletUtil.class.getSimpleName();

	public static Cookie getCookieByName(HttpServletRequest request, String name) {
		if (request == null) {
			return null;
		}
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length <= 0) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie;
			}
		}
		return null;
	}

	public static String getCookieValueByName(HttpServletRequest request, String name) {
		Cookie cookie = getCookieByName(request, name);
		if (cookie != null) {
			return cookie.getValue();
		}
		return null;
	}

	public static void dumpCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length <= 0) {
			Logger.d(TAG, "dumpCookies: empty!");
			return;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			Logger.d(TAG,
					"dumpCookies [" + i + "] name:" + cookie.getName() + "; value:" + cookie.getValue() + "; domain:"
							+ cookie.getDomain() + "; path:" + cookie.getPath() + "; maxAge:" + cookie.getMaxAge()
							+ "; version:" + cookie.getVersion() + "; comment:" + cookie.getComment() + "; secure:"
							+ cookie.getSecure());
		}
	}

	public static void cleanCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie cookie = getCookieByName(request, name);
		if (cookie != null) {
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	public static Map<String, String> getHeaders(HttpServletRequest request) {
		Enumeration<String> allNames = request.getHeaderNames();
		HashMap<String, String> headers = new HashMap<String, String>();
		while (allNames.hasMoreElements()) {
			String name = allNames.nextElement();
			headers.put(name, request.getHeader(name));
		}
		return headers;
	}

	public static void dumpParameters(HttpServletRequest request) {
		Set<String> keySet = request.getParameterMap().keySet();
		StringBuilder sb = new StringBuilder("{");
		for (String key : keySet) {
			sb.append(key).append("=").append(request.getParameter(key)).append(", ");
		}
		if (sb.length() > 2) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("}");
		Logger.d(TAG, "dumpParameters:" + sb);
	}

	public static void dumpRequest(HttpServletRequest request) {
		dumpCookies(request);
		Logger.d(TAG, "dumpHeaders:" + getHeaders(request));
		dumpParameters(request);
	}

	public static void dumpPartHeaders(Part part) {
		Collection<String> keySet = part.getHeaderNames();
		StringBuilder sb = new StringBuilder("{");
		for (String key : keySet) {
			sb.append(key).append("=").append(part.getHeader(key)).append(", ");
		}
		if (sb.length() > 2) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("}");
		Logger.d(TAG, "dumpPartHeaders:" + sb);
	}

	public static Map<String, String> getHeaders(Part part) {
		HashMap<String, String> headers = new HashMap<String, String>();
		Collection<String> keySet = part.getHeaderNames();
		for (String key : keySet) {
			headers.put(key, part.getHeader(key));
		}
		return headers;
	}

	public static Map<String, String> getContentDisposition(Part part) {
		String contentDisposition = getHeaders(part).get("content-disposition");
		HashMap<String, String> headers = new HashMap<String, String>();
		String[] strings = contentDisposition.split(";");
		for (String s : strings) {
			int index = s.indexOf("=");
			if (index != -1) {
				String key = s.substring(0, index).trim();
				String value = s.substring(index + 1).trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				}
				headers.put(key, value);
			} else {
				headers.put(s, "");
			}
		}
		return headers;
	}

	public static String getPartFileName(Part part) {
		return getContentDisposition(part).get("filename");
	}

	public static String getPartName(Part part) {
		return getContentDisposition(part).get("name");
	}
}
