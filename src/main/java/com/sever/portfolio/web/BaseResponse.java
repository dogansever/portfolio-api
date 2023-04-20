package com.sever.portfolio.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sever.portfolio.exception.BaseException;
import com.sever.portfolio.util.ExceptionUtil;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse {

    private String message;
    private Object data;

    public static BaseResponse createNew(BaseException exception) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(ExceptionUtil.convertStackTraceToString(exception));
        return baseResponse;
    }

    public static BaseResponse createNew(Object data) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(data);
        return baseResponse;
    }
}
