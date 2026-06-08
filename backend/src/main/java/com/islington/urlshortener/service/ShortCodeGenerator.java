package com.islington.urlshortener.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class ShortCodeGenerator
{

    private static final char[] ALPHABET= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final SecureRandom random= new SecureRandom();

    public String generate(int length)
    {
        char[] code= new char[length];

        for(int i=0; i< length; i++)
        {
            code[i]= ALPHABET[random.nextInt(ALPHABET.length)];
        }

        return new String(code);
    }



}
