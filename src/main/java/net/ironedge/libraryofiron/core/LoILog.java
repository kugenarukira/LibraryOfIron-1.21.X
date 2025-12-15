package net.ironedge.libraryofiron.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoILog {
    private static final Logger LOGGER = LoggerFactory.getLogger("LibraryOfIron");

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

    public static void error(String msg, Throwable t) {
        LOGGER.error(msg, t);
    }
}
