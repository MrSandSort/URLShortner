package com.islington.urlshortener.exception;

public class UnsafeUrlException extends RuntimeException {

    public UnsafeUrlException(String reason)
    {
        super(reason);
    }
}
