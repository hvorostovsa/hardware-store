package utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object TokenManager {
    private const val SECRET = "46456544"
    private const val ISSUER = "server"
    private const val EXPIRATION_TIME = 600_000L

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(username: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(algorithm)
    }

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .build()
}