package com.sever.portfolio;

import com.sever.portfolio.exception.BaseException;
import com.sever.portfolio.util.ExceptionUtil;
import lombok.Data;

@Data
public class BaseResponse {

    private String message;

    public static BaseResponse createNew(BaseException exception) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(ExceptionUtil.convertStackTraceToString(exception));
        return baseResponse;
    }
}
