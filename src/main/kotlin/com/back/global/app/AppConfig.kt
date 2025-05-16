package com.back.global.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AppConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    companion object {
        private lateinit var environment: Environment
        private lateinit var objectMapper: ObjectMapper
        private lateinit var siteCookieDomain: String

        fun isProd(): Boolean = environment.matchesProfiles("prod")

        fun isDev(): Boolean = environment.matchesProfiles("dev")

        fun isTest(): Boolean = environment.matchesProfiles("test")

        fun isNotProd(): Boolean = !isProd()

        fun getObjectMapper(): ObjectMapper = objectMapper

        fun getSiteCookieDomain(): String = siteCookieDomain
    }

    @Autowired
    fun setEnvironment(environment: Environment) {
        Companion.environment = environment
    }

    @Autowired
    fun setObjectMapper(objectMapper: ObjectMapper) {
        Companion.objectMapper = objectMapper
    }

    @Value("\${custom.site.cookieDomain}")
    fun setSiteCookieDomain(siteCookieDomain: String) {
        Companion.siteCookieDomain = siteCookieDomain
    }
}