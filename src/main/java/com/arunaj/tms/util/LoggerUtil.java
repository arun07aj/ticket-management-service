package com.arunaj.tms.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class LoggerUtil {
    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(cls);
    }
}
