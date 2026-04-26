package com.app.book.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Objects;

import com.app.book.dto.User;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtService {

    // In production, load this from a secure .pem file or KeyStore
    private final RSAPrivateKey privateKey = loadPrivateKey();

    public String generateToken(User user) throws Exception {
    	var now = LocalDateTime.now(ZoneOffset.UTC);
    	var expiry = now.plusHours(1);
    	long currSeconds = now.atZone(ZoneOffset.UTC).toEpochSecond();
    	long expSeconds = expiry.atZone(ZoneOffset.UTC).toEpochSecond();
        var signer = new RSASSASigner(privateKey);
        var claimsSet = new JWTClaimsSet.Builder()
            .issuer("https://authbook.dev")
            .subject(user.username())
            .claim("upn", user.username())
            .claim("groups", user.roles())
            .claim("iat", currSeconds) 
            .claim("exp", expSeconds)
            .build();
        var signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("kid-1").build(),
            claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
    
    private RSAPrivateKey loadPrivateKey() {
        try (var is = getClass().getResourceAsStream("/privateKey.pem")) {
            if (Objects.isNull(is)) {
                throw new RuntimeException("Private key file not found in resources!");
            }
            // 1. Read file and remove PEM headers/footers/newlines
            var key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            // 2. Decode from Base64
            var decode = Base64.getDecoder().decode(key);
            // 3. Create the KeySpec and generate the RSAPrivateKey
            var keySpec = new PKCS8EncodedKeySpec(decode);
            var kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(keySpec);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }
}

