package com.back.standard.util

import com.back.global.app.AppConfig
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

object Ut {
    object str {
        @JvmStatic
        fun isBlank(str: String?): Boolean {
            return str == null || str.trim().isEmpty()
        }

        @JvmStatic
        fun lcfirst(str: String): String {
            return str[0].lowercaseChar() + str.substring(1)
        }

        @JvmStatic
        fun isNotBlank(str: String?): Boolean {
            return !isBlank(str)
        }
    }

    object json {
        private val om: ObjectMapper = AppConfig.getObjectMapper()

        @JvmStatic
        fun toString(obj: Any): String {
            return om.writeValueAsString(obj)
        }
    }


    object jwt {
        @JvmStatic
        fun toString(secret: String, expireSeconds: Long, body: Map<String, Any>): String {
            val issuedAt = Date()
            val expiration = Date(issuedAt.time + 1000L * expireSeconds)

            val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

            return Jwts.builder()
                .claims(body)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact()
        }

        @JvmStatic
        fun isValid(secret: String, jwtStr: String): Boolean {
            val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

            return try {
                Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(jwtStr)
                true
            } catch (e: Exception) {
                false
            }
        }

        @JvmStatic
        fun payload(secret: String, jwtStr: String): Map<String, Any>? {
            val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

            return try {
                @Suppress("UNCHECKED_CAST")
                Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(jwtStr)
                    .payload as Map<String, Any>?
            } catch (e: Exception) {
                null
            }
        }
    }
}