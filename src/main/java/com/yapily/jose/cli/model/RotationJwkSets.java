package com.yapily.jose.cli.model;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RotationJwkSets {
    private JWKSet validKeys = new JWKSet();
    private JWKSet expiredKeys = new JWKSet();
    private JWKSet revokedKeys = new JWKSet();

    public RotationJwkSets addValidKey(JWK jwk) {
        this.validKeys = new JWKSet(Stream.concat(validKeys.getKeys().stream(), Stream.of(jwk)).collect(Collectors.toList()));
        return this;
    }
}
