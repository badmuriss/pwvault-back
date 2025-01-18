package com.outis.pwvault.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outis.pwvault.model.JwtPayload;
import com.outis.pwvault.util.CryptoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {

    private final String secret;

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    private CryptoUtil cryptoUtil;
    private ObjectMapper objectMapper;

    public JwtService(@Value("${JWT_SECRET}") String secret, CryptoUtil cryptoUtil, ObjectMapper objectMapper) {
        this.secret = secret;
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
        this.cryptoUtil = cryptoUtil;
        this.objectMapper = objectMapper;
    }

    public String generateToken(Map<String, Object> claims) {
        return JWT.create()
                .withPayload(claims)
                .withIssuedAt(new Date())
                .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    public DecodedJWT decodeToken(String token) throws JWTDecodeException {
        return JWT.decode(token);
    }

    public String getPayload(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getClaims().get("data").asString();
    }

    public JwtPayload getDecryptedPayload(String token) throws Exception {
        DecodedJWT decodedJWT = validateToken(token);
        String encryptedPayload = decodedJWT.getClaims().get("data").asString();
        String payloadJSON = cryptoUtil.decryptWithAES(encryptedPayload);
        return objectMapper.readValue(payloadJSON, JwtPayload.class);
    }
}
