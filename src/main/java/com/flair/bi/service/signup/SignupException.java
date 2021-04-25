package com.flair.bi.service.signup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignupException extends RuntimeException {

    public SignupException(Type error) {
        super(error.name());
        this.error = error;
    }

    private final Type error;

    public enum Type {
        USERNAME_EXISTS, EMAIL_EXISTS
    }

}
