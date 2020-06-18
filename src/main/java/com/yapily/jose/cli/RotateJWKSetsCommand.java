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
        name = "rotate-jwks",
        description = "Rotate a set of keys, represented as JWK"
)
@Slf4j
public class RotateJWKSetsCommand implements Callable<Integer> {

    public static final String VALID_KEYS_FILE_NAME = "valid-keys.json";
    public static final String EXPIRED_KEYS_FILE_NAME = "expired-keys.json";
    public static final String REVOKED_KEYS_FILE_NAME = "revoked-keys.json";

    @CommandLine.Option(names = {"-k", "--key-path"}, required = true)
    private String keysPath;
    @CommandLine.Option(names = {"-o", "--output"}, required = true)
    private String rotatedKeysPath;
    @CommandLine.Option(names = {"-t", "--type"}, required = false, defaultValue = "RSA")
    private KeyType type;
    @CommandLine.Option(names = {"-s", "--key-size"}, required = false, defaultValue = "4096")
    private int keySize;
    @CommandLine.Option(names = {"-c", "--curve-name"}, required = false, defaultValue = "P-256")
    private Curve curve;
    @CommandLine.Option(names = {"-r", "--revoke"}, required = false, defaultValue = "false")
    private Boolean revokedAllKeys;

    private RotationJwkSets currentJwkSets;

    @Autowired
    private RotateJwkSetsService rotateJwkSetsService;
    @Autowired
    private JwkSetsLoaderService jwkSetsLoaderService;

    @Override
    public Integer call() throws IOException {
        this.currentJwkSets = validateEntries();

        try {
            RotationJwkSets rotatedJwkSets;
            if (revokedAllKeys) {
                log.info("Revoke the keys");
                rotatedJwkSets = rotateJwkSetsService.revoke(currentJwkSets);
            } else {
                log.info("Rotate the keys");
                rotatedJwkSets = rotateJwkSetsService.rotate(currentJwkSets);
            }

            log.info("Create new keys");
            rotatedJwkSets
                    .addValidKey(rotateJwkSetsService.createNewJWK(type, keySize, KeyUse.SIGNATURE, curve))
                    .addValidKey(rotateJwkSetsService.createNewJWK(type, keySize, KeyUse.ENCRYPTION, curve))
            ;

            log.info("Save rotated JWK sets into '{}'", rotatedKeysPath);
            jwkSetsLoaderService.outputJwkSets(rotatedKeysPath, rotatedJwkSets);
            log.info("Key rotation completed.");
            return 0;
        } catch (Exception e) {
            log.error("Key rotation failed", e);
            return -1;
        }
    }

    private RotationJwkSets validateEntries() throws IOException {
        if (type != KeyType.EC && type != KeyType.RSA) {
            throw new IllegalArgumentException("Key type '" + type +"' is not supported.");
        }
        return jwkSetsLoaderService.loadKeys(keysPath);
    }

}