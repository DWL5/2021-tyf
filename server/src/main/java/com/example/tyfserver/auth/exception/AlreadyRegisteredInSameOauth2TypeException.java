package com.example.tyfserver.auth.exception;

import com.example.tyfserver.common.dto.ErrorResponse;
import com.example.tyfserver.common.dto.ErrorWithTokenResponse;
import com.example.tyfserver.common.exception.BaseException;

public class AlreadyRegisteredInSameOauth2TypeException extends BaseException {

    public static final String ERROR_CODE = "auth-004";
    private static final String MESSAGE = "이미 가입되어 있는 사용자입니다. 토큰첨부.";

    private final String token; // todo 필요없음. 제거

    public AlreadyRegisteredInSameOauth2TypeException(String token) {
        super(ERROR_CODE, MESSAGE);
        this.token = token;
    }

    @Override
    public ErrorResponse toResponse() {
        return new ErrorWithTokenResponse(getErrorCode(), getMessage(), token);
    }
}
