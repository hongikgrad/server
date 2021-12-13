package com.hongikgrad;

import com.hongikgrad.common.hash.SHA256;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class HashTest {

    @Test
    public void hashtest() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        SHA256 hash1 = new SHA256();
        SHA256 hash2 = new SHA256();
        String sid = "b615125";
        var hashedSID = hash1.hash(sid);
        Assertions.assertThat(hashedSID).isEqualTo(hash2.hash(sid));
    }
}
