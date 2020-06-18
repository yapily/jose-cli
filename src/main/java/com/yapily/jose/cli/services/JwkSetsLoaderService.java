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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nimbusds.jose.jwk.JWKSet;
import com.yapily.jose.cli.model.RotationJwkSets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

@Service
@Slf4j
public class JwkSetsLoaderService {

    public static final String VALID_KEYS_FILE_NAME = "valid-keys.json";
    public static final String EXPIRED_KEYS_FILE_NAME = "expired-keys.json";
    public static final String REVOKED_KEYS_FILE_NAME = "revoked-keys.json";

    public RotationJwkSets loadKeys(String keysPath) throws IOException {
        return RotationJwkSets.builder()
                .validKeys(verifyAndLoadJWKSet(keysPath + File.separator + VALID_KEYS_FILE_NAME))
                .expiredKeys(verifyAndLoadJWKSet(keysPath + File.separator + EXPIRED_KEYS_FILE_NAME))
                .revokedKeys(verifyAndLoadJWKSet(keysPath + File.separator + REVOKED_KEYS_FILE_NAME))
                .build();
    }

    public void outputJwkSets(String keyPath, RotationJwkSets jwkSets) throws IOException {
        File directory = new File(keyPath);
        if (! directory.exists()){
            directory.mkdirs();
        }

        writeJwkSetToFile(jwkSets.getValidKeys(), keyPath + File.separator + VALID_KEYS_FILE_NAME);
        writeJwkSetToFile(jwkSets.getExpiredKeys(), keyPath + File.separator + EXPIRED_KEYS_FILE_NAME);
        writeJwkSetToFile(jwkSets.getRevokedKeys(), keyPath + File.separator + REVOKED_KEYS_FILE_NAME);
    }

    private void writeJwkSetToFile(JWKSet jwkSet, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(jwkSet.toJSONObject(false).toJSONString());
        writer.write(gson.toJson(je));
        writer.close();
    }

    private JWKSet verifyAndLoadJWKSet(String filePath) throws IOException {
        File file = getFile(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File '" + file.getPath() + "' doesn't exist.");
        }
        try {
            return JWKSet.load(file);
        } catch (IOException | ParseException e) {
            throw new IllegalArgumentException("File '" + file.getPath() + "' content is not following the JOSE JWK set standard format.");
        }
    }

    protected File getFile(String filePath) throws IOException {
        return new File(filePath);
    }
}
