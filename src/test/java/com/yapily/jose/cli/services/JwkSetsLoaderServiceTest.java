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