package org.coupon.api.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String message){
        super(message);
    }

}