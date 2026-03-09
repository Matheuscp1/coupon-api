package org.coupon.api.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericErrors handleBusiness(BusinessException ex){
        return new GenericErrors(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericErrors handleRuntimeException(RuntimeException ex){
        return new GenericErrors(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericErrors handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(err -> err.getPropertyPath() + ": " + err.getMessage())
                .toList();
        return new GenericErrors(errors);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericErrors handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        return new GenericErrors(errors);
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericErrors handleTransactionSystemException(TransactionSystemException ex) {

        Throwable cause = ex.getRootCause();

        if (cause instanceof ConstraintViolationException violationException) {

            List<String> errors = violationException.getConstraintViolations()
                    .stream()
                    .map(err -> err.getPropertyPath() + ": " + err.getMessage())
                    .toList();

            return new GenericErrors(errors);
        }

        return new GenericErrors("Transaction error");
    }


}