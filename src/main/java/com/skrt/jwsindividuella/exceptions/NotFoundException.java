package com.skrt.jwsindividuella.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException  extends RuntimeException{

    private final String resource;
    private final String fieldName;
    private final Object fieldValue;

    public NotFoundException(String resource,String fieldName, Object fieldValue){
        super("%s with %s '%s' was not found".formatted(resource, fieldName, fieldValue));
        this.resource = resource;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResource() {
        return resource;
    }
    public String getFieldName() {return fieldName;}
    public Object getFieldValue() {
        return fieldValue;
    }
}
