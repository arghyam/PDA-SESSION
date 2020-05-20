package com.socion.session.exceptions;


public class ValidationError {

    private String field;
    private String message;

    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return this.field +" "+ message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
