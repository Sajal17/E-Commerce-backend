package com.sa.M_Mart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException{

    private final int statusCode;
    public ApiException(String message, int statusCode) {

        super(message);
        this.statusCode = statusCode;
    }
}
