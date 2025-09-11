package com.skrt.jwsindividuella.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenOperationException extends RuntimeException {

    private final String userSub;
    private final String resource;
    private final Object fieldValue;

    public ForbiddenOperationException(String userSub, String resource, Object fieldValue) {
        super("User with sub '%s' is not allowed to modify %s with id '%s'".formatted(userSub, resource, fieldValue));
        this.userSub = userSub;
        this.resource = resource;
        this.fieldValue = fieldValue;
    }


    public String getUserSub() {
        return userSub;
    }
    public String getResource() {
        return resource;
    }
    public Object getFieldValue() {
        return fieldValue;
    }
}
