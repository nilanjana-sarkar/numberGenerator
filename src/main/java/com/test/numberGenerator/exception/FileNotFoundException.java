package com.test.numberGenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND,reason = "File does not exist")
public class FileNotFoundException extends Exception {
    public FileNotFoundException() {
        super();
    }
}
