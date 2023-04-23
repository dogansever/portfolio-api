package com.sever.portfolio.web;

import com.sever.portfolio.exception.BaseException;
import com.sever.portfolio.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class BaseControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ExceptionUtil.convertStackTraceToString(ex));
        return new ResponseEntity<>(BaseResponse.createNew(ex), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ExceptionUtil.convertStackTraceToString(ex));
        return new ResponseEntity<>(BaseResponse.createNew(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<Object> exception(BaseException ex) {
        log.error(ExceptionUtil.convertStackTraceToString(ex));
        return new ResponseEntity<>(BaseResponse.createNew(ex), HttpStatus.BAD_REQUEST);
    }
}
