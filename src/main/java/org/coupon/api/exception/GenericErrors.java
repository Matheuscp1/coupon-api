package org.coupon.api.exception;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class GenericErrors {
    @Getter
    private List<String> errors;

    public GenericErrors(String messageError) {
        this.errors = Arrays.asList(messageError);
    }

    public GenericErrors(List<String> errors) {
        this.errors = errors;
    }
}