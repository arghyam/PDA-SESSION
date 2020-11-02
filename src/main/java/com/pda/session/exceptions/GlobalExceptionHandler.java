package com.pda.session.exceptions;

import com.pda.session.dto.ResponseDTO;
import com.pda.session.utils.HttpUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity handleUnauthorizedException(UnauthorizedException e,
                                                      HttpServletResponse response) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserCreateException.class)
    public ResponseEntity handleUnableToCreateUserException(UserCreateException e,
                                                            HttpServletResponse response) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity habdleInvaidOtpException(InvalidOtpException e,
                                                   HttpServletResponse response) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UnprocessableEntitiesException.class)
    public ResponseEntity handleUnprocessableException(UnprocessableEntitiesException e,
                                                       HttpServletResponse response) {
        return new ResponseEntity<>(new ValidationErrorResponse(e.getErrors()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseDTO handleNotFoundException(NotFoundException e,
                                               HttpServletResponse response) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO = HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return responseDTO;
    }

}
