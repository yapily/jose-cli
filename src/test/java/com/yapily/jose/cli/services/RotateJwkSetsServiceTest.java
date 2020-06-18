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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.yapily.jose.cli.model.RotationJwkSets;
import org.junit.jupiter.api.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RotateJwkSetsServiceTest {

    @Test
    void rotate() throws JOSEException {
        //GIVEN
        RotateJwkSetsService rotateJwkSetsService = new RotateJwkSetsService();
        RotationJwkSets initialJwkSetsForTest = RotationJwkSets.builder()
                .validKeys(new JWKSet(
                        Stream.of(
                                createFakeKey("a", KeyUse.SIGNATURE),
                                createFakeKey("b", KeyUse.ENCRYPTION)
                        ).collect(Collectors.toList())))
                .expiredKeys(new JWKSet(
                        Stream.of(
                                createFakeKey("c", KeyUse.SIGNATURE),
                                createFakeKey("d", KeyUse.ENCRYPTION)
                        ).collect(Collectors.toList())))
                .revokedKeys(new JWKSet(
                        Stream.of(
                                createFakeKey("e", KeyUse.SIGNATURE),
                                createFakeKey("f", KeyUse.ENCRYPTION)
                        ).collect(Collectors.toList())))
                .build();

        //WHEN
        RotationJwkSets rotatedJwkSets = rotateJwkSetsService.rotate(initialJwkSetsForTest);

        //THEN
        assertThat(rotatedJwkSets.getValidKeys().getKeys().isEmpty()).isTrue();
        assertThat(rotatedJwkSets.getExpiredKeys().getKeys().stream().map(JWK::getKeyID).collect(Collectors.toList())).isEqualTo(Stream.of("a", "b", "c", "d").collect(Collectors.toList()));
        assertThat(rotatedJwkSets.getRevokedKeys()).isEqualTo(initialJwkSetsForTest.getRevokedKeys());
    }

    @Test
    void revoke() throws JOSEException {
        //GIVEN
        RotateJwkSetsService rotateJwkSetsService = new RotateJwkSetsService();
        RotationJwkSets initialJwkSetsForTest = RotationJwkSets.builder()
                .validKeys(new JWKSet(
                        Stream.of(
                                createFakeKey("a", KeyUse.SIGNATURE),
                                createFakeKey("b", KeyUse.ENCRYPTION)
                        ).collect(Collectors.toList())))
                .expiredKeys(new JWKSet(
                        Stream.of(
                                createFakeKey("c", KeyUse.SIGNATURE),
                                createFakeKey("d", KeyUse.ENCRYPTION)
                        ).collect(Collectors.toList())))
                .revokedKeys(new JWKSet(
                        Stream.of(
                                createFakeKey("e", KeyUse.SIGNATURE),
                                createFakeKey("f", KeyUse.ENCRYPTION)
                        ).collect(Collectors.toList())))
                .build();

        //WHEN
        RotationJwkSets rotatedJwkSets = rotateJwkSetsService.revoke(initialJwkSetsForTest);

        //THEN
        assertThat(rotatedJwkSets.getValidKeys().getKeys().isEmpty()).isTrue();
        assertThat(rotatedJwkSets.getExpiredKeys().getKeys().isEmpty()).isTrue();
        assertThat(rotatedJwkSets.getRevokedKeys().getKeys().stream().map(JWK::getKeyID).collect(Collectors.toList())).isEqualTo(Stream.of("a", "b", "c", "d", "e", "f").collect(Collectors.toList()));
    }

    @Test
    void createNewECKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, JOSEException {
        //GIVEN
        RotateJwkSetsService rotateJwkSetsService = new RotateJwkSetsService();

        //WHEN
        JWK jwk = rotateJwkSetsService.createNewJWK(KeyType.EC, 0, KeyUse.SIGNATURE, Curve.P_256);

        //THEN
        assertThat(jwk.getKeyType()).isEqualTo(KeyType.EC);
        assertThat(jwk.getKeyUse()).isEqualTo(KeyUse.SIGNATURE);
    }

    @Test
    void createNewRSAKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, JOSEException {
        //GIVEN
        RotateJwkSetsService rotateJwkSetsService = new RotateJwkSetsService();

        //WHEN
        JWK jwk = rotateJwkSetsService.createNewJWK(KeyType.RSA, 2048, KeyUse.SIGNATURE, null);

        //THEN
        assertThat(jwk.getKeyType()).isEqualTo(KeyType.RSA);
        assertThat(jwk.getKeyUse()).isEqualTo(KeyUse.SIGNATURE);
    }

    private JWK createFakeKey(String kid, KeyUse use) throws JOSEException {
        return new RSAKeyGenerator(2048)
                .keyUse(use)
                .keyID(kid)
                .generate();
    }
}