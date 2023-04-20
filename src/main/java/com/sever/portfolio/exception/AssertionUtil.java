package com.sever.portfolio.exception;

import java.util.List;
import java.util.Optional;

public class AssertionUtil {
    private static final String DATA_NOT_FOUND = "Data not found";

    public static void assertTrue(boolean mustBeTrue) {
        if (!mustBeTrue) {
            throw new BaseException();
        }
    }

    public static void assertDataNotFound(Object data) {
        if (data == null) {
            throw new BaseException(DATA_NOT_FOUND);
        }
        if (data instanceof Optional && ((Optional<?>) data).isEmpty()) {
            throw new BaseException(DATA_NOT_FOUND);
        }
        if (data instanceof List && ((List<?>) data).isEmpty()) {
            throw new BaseException(DATA_NOT_FOUND);
        }
    }

}
