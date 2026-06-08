package com.islington.urlshortener.exception;

public class AliasAlreadyExistsException extends RuntimeException {

    public AliasAlreadyExistsException(String alias) {
        super("Short URL alias already exists: " + alias);
    }
}
