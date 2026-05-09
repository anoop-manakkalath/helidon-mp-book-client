package com.app.book.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

import com.app.book.dto.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtService {

    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        this.privateKey = loadPrivateKey();
    }

    public String generateToken(User user) {
        try {
            var now = Instant.now(); 
            var expiry = now.plus(1, ChronoUnit.HOURS);
            var signer = new RSASSASigner(privateKey);
            var claimsSet = new JWTClaimsSet.Builder()
                        .issuer("https://authbook.dev")
                        .subject(user.username())
                        .claim("upn", user.username())
                        .claim("groups", user.roles())
                        .issueTime(Date.from(now))      
                        .expirationTime(Date.from(expiry))
                        .build();
            var signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("kid-1").build(), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        }
        catch (JOSEException e) {
            throw new SecurityException("Failed to sign JWT", e);
        }
    }
    
    private PrivateKey loadPrivateKey() {
        // No need for manual caching logic; @PostConstruct handles the lifecycle
        try (var is = getClass().getResourceAsStream("/privateKey.pem")) {
            if (Objects.isNull(is)) {
                throw new SecurityException("Private key file not found!");
            }
            var rawPem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var encodedKey = rawPem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            var keyBytes = Base64.getDecoder().decode(encodedKey);
            var keySpec = new PKCS8EncodedKeySpec(keyBytes);
            var kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        }
        catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new SecurityException("Could not initialize PrivateKey", e);
        }
    }
}
