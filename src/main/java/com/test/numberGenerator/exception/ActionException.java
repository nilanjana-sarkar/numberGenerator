package com.test.numberGenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,reason = "Please enter valid action")
public class ActionException extends Exception {
    public ActionException() {
        super();
    }
}
