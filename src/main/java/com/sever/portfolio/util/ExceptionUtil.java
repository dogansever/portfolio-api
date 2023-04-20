package com.sever.portfolio.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by U083480 on 22.08.2017.
 */
public class ExceptionUtil {
    public static String convertStackTraceToString(Throwable exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
