/**
 * Copyright 2020 Yapily
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.yapily.jose.cli.services;

import com.nimbusds.jose.jwk.JWK;
import com.yapily.jose.cli.model.RotationJwkSets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwkSetsLoaderServiceTest {

    @Test
    void loadKeys() throws IOException {
        //GIVEN
        JwkSetsLoaderService jwkSetsLoaderService = new JwkSetsLoaderService() {
            @Override
            protected File getFile(String filePath) throws IOException {
                return new ClassPathResource(filePath).getFile();
            }
        };

        //WHEN
        RotationJwkSets jwkSets = jwkSetsLoaderService.loadKeys("keys");

        //THEN
        assertThat(jwkSets.getValidKeys().getKeys().stream().map(JWK::getKeyID).collect(Collectors.toList())).isEqualTo(Stream.of("a", "b").collect(Collectors.toList()));
        assertThat(jwkSets.getExpiredKeys().getKeys().stream().map(JWK::getKeyID).collect(Collectors.toList())).isEqualTo(Stream.of("c", "d").collect(Collectors.toList()));
        assertThat(jwkSets.getRevokedKeys().getKeys().stream().map(JWK::getKeyID).collect(Collectors.toList())).isEqualTo(Stream.of("e", "f").collect(Collectors.toList()));
    }
}