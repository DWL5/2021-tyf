package com.example.tyfserver.member.exception;

import com.example.tyfserver.common.exception.BaseException;

public class PageNameValidationRequestException extends BaseException {

    public static final String ERROR_CODE = "member-002";
    private static final String MESSAGE = "PageName 형식이 올바르지 않습니다.";

    public PageNameValidationRequestException() {
        super(ERROR_CODE, MESSAGE);
    }
}
