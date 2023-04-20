package com.sever.portfolio.exception;

public class BaseException extends RuntimeException {
    public BaseException(Exception e) {
        super(e);
    }

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }
}
