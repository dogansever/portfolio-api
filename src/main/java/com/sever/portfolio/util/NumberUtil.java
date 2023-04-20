package com.sever.portfolio.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumberUtil {
    public static Double valueOfDouble(String value) {
        try {
            return Double.valueOf(value.replaceAll("\\.", "").replaceAll(",", "."));
        } catch (Exception e) {
            return 0d;
        }
    }

}
