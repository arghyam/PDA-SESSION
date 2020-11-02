package com.pda.session.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableEntitiesException extends RuntimeException  {

    private final List<ValidationError> errors;

    public UnprocessableEntitiesException(List<ValidationError> errors) {
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

}
