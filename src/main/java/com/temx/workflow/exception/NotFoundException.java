package com.temx.workflow.exception;

//@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

    public NotFoundException(String message){
        super(message);
    }


}
