/**
 * Copyright 2020 Yapily
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.yapily.jose.cli;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.yapily.jose.cli.model.RotationJwkSets;
import com.yapily.jose.cli.services.JwkSetsLoaderService;
import com.yapily.jose.cli.services.RotateJwkSetsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@Component
@CommandLine.Command(
        name = "init",
        description = "Initialise the jwks sets with some valid keys"
)
@Slf4j
public class JWKSetsInitCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-o", "--output"}, required = true)
    private String keysPath;
    @CommandLine.Option(names = {"-t", "--type"}, required = false, defaultValue = "RSA")
    private KeyType type;
    @CommandLine.Option(names = {"-s", "--key-size"}, required = false, defaultValue = "4096")
    private int keySize;
    @CommandLine.Option(names = {"-c", "--curve-name"}, required = false, defaultValue = "P-256")
    private Curve curve;

    private RotationJwkSets currentJwkSets;

    @Autowired
    private RotateJwkSetsService rotateJwkSetsService;
    @Autowired
    private JwkSetsLoaderService jwkSetsLoaderService;

    @Override
    public Integer call() throws IOException {
        if (type != KeyType.EC && type != KeyType.RSA) {
            throw new IllegalArgumentException("Key type '" + type +"' is not supported.");
        }
        this.currentJwkSets = new RotationJwkSets();

        try {
            log.info("Create new keys");
            currentJwkSets
                    .addValidKey(rotateJwkSetsService.createNewJWK(type, keySize, KeyUse.SIGNATURE, curve))
                    .addValidKey(rotateJwkSetsService.createNewJWK(type, keySize, KeyUse.ENCRYPTION, curve))
            ;

            log.info("Save JWKs sets into '{}'", keysPath);
            jwkSetsLoaderService.outputJwkSets(keysPath, currentJwkSets);
            log.info("JWKS sets created.");
            return 0;
        } catch (Exception e) {
            log.error("JWKS sets creation failed", e);
            return -1;
        }
    }
}