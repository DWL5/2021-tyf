package com.example.tyfserver.payment.exception;

import com.example.tyfserver.common.exception.BaseException;

public class RefundGuaranteeDurationException extends BaseException {

    public static final String ERROR_CODE = "refund-003";
    private static final String MESSAGE = "환불 기간이 지났습니다.";

    public RefundGuaranteeDurationException() {
        super(ERROR_CODE, MESSAGE);
    }
}
