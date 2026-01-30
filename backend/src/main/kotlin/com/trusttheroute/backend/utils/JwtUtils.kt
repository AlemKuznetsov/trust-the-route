package com.trusttheroute.backend.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

object JwtUtils {
    private val secret = System.getenv("JWT_SECRET") ?: "your_secret_key_minimum_32_characters_long"
    private const val ISSUER = "trust-the-route"
    private const val AUDIENCE = "trust-the-route-users"
    private val algorithm = Algorithm.HMAC256(secret)
    
    fun generateToken(userId: String, email: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 часа
            .sign(algorithm)
    }
    
    fun verifyToken(token: String): DecodedJWT? {
        return try {
            JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .build()
                .verify(token)
        } catch (e: Exception) {
            null
        }
    }
}
