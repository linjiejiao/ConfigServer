package cn.ljj.util;

public class Logger {
    protected static final int LOG_LEVEL_DEBUG = 1;
    protected static final int LOG_LEVEL_INFO = 2;
    protected static final int LOG_LEVEL_WARNING = 3;
    protected static final int LOG_LEVEL_ERROR = 4;

    private static int currentLogLevel = LOG_LEVEL_DEBUG;

    public static int d(String tag, String msg) {
        if (LOG_LEVEL_DEBUG < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg);
        return 1;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL_DEBUG < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg, tr);
        return 1;
    }

    public static int i(String tag, String msg) {
        if (LOG_LEVEL_INFO < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg);
        return 1;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL_INFO < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg, tr);
        return 1;
    }

    public static int w(String tag, String msg) {
        if (LOG_LEVEL_WARNING < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg);
        return 1;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL_WARNING < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg, tr);
        return 1;
    }

    public static int e(String tag, String msg) {
        if (LOG_LEVEL_ERROR < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg);
        return 1;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL_ERROR < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg, tr);
        return 1;
    }
}
