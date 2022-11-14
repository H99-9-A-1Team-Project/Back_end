package com.example.backend.global.exception.customexception;

import com.example.backend.global.exception.CustomException;
import com.example.backend.global.exception.ErrorCode;

public class LoginFailureException extends CustomException {
    public LoginFailureException() {
        super(ErrorCode.LOGIN_FAIL_EXCEPTION);
    }
}
