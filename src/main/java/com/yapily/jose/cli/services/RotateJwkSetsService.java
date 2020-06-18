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
import com.yapily.jose.cli.model.RotationJwkSets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RotateJwkSetsService {

    public RotationJwkSets rotate(RotationJwkSets currentJwkSets) {
        log.info("Move all the valid keys as expired.");
        return RotationJwkSets.builder()
                .validKeys(new JWKSet())
                .expiredKeys(new JWKSet(
                        Stream.of(
                                currentJwkSets.getValidKeys().getKeys().stream(),
                                currentJwkSets.getExpiredKeys().getKeys().stream()
                        ).flatMap(i -> i).collect(Collectors.toList()))
                )
                .revokedKeys(currentJwkSets.getRevokedKeys())
                .build();
    }

    public RotationJwkSets revoke(RotationJwkSets currentJwkSets) {
        log.info("Move all the valid and expired keys into the revoke list.");
        return RotationJwkSets.builder()
                .validKeys(new JWKSet())
                .expiredKeys(new JWKSet())
                .revokedKeys(new JWKSet(
                        Stream.of(
                                currentJwkSets.getValidKeys().getKeys().stream(),
                                currentJwkSets.getExpiredKeys().getKeys().stream(),
                                currentJwkSets.getRevokedKeys().getKeys().stream()
                        ).flatMap(i -> i).collect(Collectors.toList()))
                )
                .build();
    }

    public JWK createNewJWK(KeyType type, int keySize, KeyUse keyUse, Curve curve) throws NoSuchAlgorithmException, JOSEException, InvalidAlgorithmParameterException {
        log.info("Create a new {} key for '{}'", type, keyUse);
        // Generate the RSA key pair
        KeyPairGenerator gen = KeyPairGenerator.getInstance(type.getValue());

       if (type == KeyType.EC) {
           gen.initialize(curve.toECParameterSpec());
           KeyPair keyPair = gen.generateKeyPair();
            // Convert to JWK format
           return new ECKey.Builder(curve, (ECPublicKey) keyPair.getPublic())
                   .privateKey((ECPrivateKey) keyPair.getPrivate())
                   .keyUse(keyUse)
                   .keyID(UUID.randomUUID().toString())
                   .build();
       } else if (type == KeyType.RSA) {
           gen.initialize(keySize);
           KeyPair keyPair = gen.generateKeyPair();
           return new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
                   .privateKey((RSAPrivateKey)keyPair.getPrivate())
                   .keyUse(keyUse)
                   .keyID(UUID.randomUUID().toString())
                   .build();
       } else {
           throw new IllegalArgumentException("Type '" + type +"' is not supported.");
       }
    }
}
