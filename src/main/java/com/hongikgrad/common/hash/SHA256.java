package com.hongikgrad.common.hash;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class SHA256 {

    public String hash(String studentId) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(studentId.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = md.digest();
        return String.format("%064x", new BigInteger(1, bytes));
    }

}
