package com.socion.session.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties("message")
public class ValidationErrorResponse extends ResponseMessage {


    private List<ValidationError> errors;

    ValidationErrorResponse(List<ValidationError> errors) {
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
}
