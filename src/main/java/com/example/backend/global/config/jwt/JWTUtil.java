package com.example.backend.global.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.global.exception.CustomException;
import com.example.backend.global.exception.ErrorCode;
import com.example.backend.global.security.auth.UserDetailsImpl;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;

public class JWTUtil {


    public static final Algorithm ALGORITHM = Algorithm.HMAC256("hahaha");
    private static final long AUTH_TIME = 60 * 60 * 3;
    private static final long REFRESH_TIME = 60 * 60 * 24 * 7;

    public String makeAuthToken(UserDetailsImpl userDetails) {

        return JWT.create()
                .withSubject(userDetails.getUser().getEmail())
                .withClaim("exp", Instant.now().getEpochSecond() + AUTH_TIME)
                .sign(ALGORITHM);
    }

    public String makeRefreshToken(UserDetailsImpl userDetails) {

        return JWT.create()
                .withSubject(userDetails.getUser().getEmail())
                .withClaim("exp", Instant.now().getEpochSecond() + REFRESH_TIME)
                .sign(ALGORITHM);
    }

    public static VerifyResult verify(String token) {
        try {
            DecodedJWT verify = JWT.require(ALGORITHM).build().verify(token);
            return VerifyResult.builder().success(true)
                    .userId(verify.getSubject()).build();

        } catch (Exception ex) {
            DecodedJWT decode = JWT.decode(token);
            return VerifyResult.builder().success(false)
                    .userId(decode.getSubject()).build();
        }
    }


}
