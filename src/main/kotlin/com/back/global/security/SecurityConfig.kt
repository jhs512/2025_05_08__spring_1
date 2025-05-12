package com.back.global.security

import com.back.global.rsData.RsData
import com.back.standard.base.Empty
import com.back.standard.util.Ut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler

@Configuration
class SecurityConfig(

) {
    @Bean
    fun baseSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(HttpMethod.GET, "/api/*/posts/{id:\\d+}", permitAll)
                authorize(HttpMethod.GET, "/api/*/posts", permitAll)
                authorize(HttpMethod.GET, "/api/*/posts/{postId:\\d+}/comments", permitAll)
                authorize(HttpMethod.GET, "/api/*/posts/{postId:\\d+}/genFiles", permitAll)
                authorize(HttpMethod.GET, "/api/*/posts/{postId:\\d+}/genFiles/{id:\\d+}", permitAll)
                authorize("/api/*/members/login", permitAll)
                authorize("/api/*/members/logout", permitAll)
                authorize("/api/*/members/join", permitAll)
                authorize("/api/*/**", authenticated)
                authorize(anyRequest, permitAll)
            }

            // h2-console 을 사용하기 위한 설정
            headers {
                frameOptions {
                    sameOrigin = true
                }
            }

            // api 에는 원래 csrf 를 사용하지 않음
            // h2-console 을 위해서 csrf 를 비활성화
            csrf { disable() }

            // api 서버에서는 딱히 폼 로그인을 하지 않는다.
            formLogin { disable() }

            // api 서버에서는 기본적으로 세션을 사용하지 않는다.
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            exceptionHandling {
                authenticationEntryPoint = AuthenticationEntryPoint { request, response, authException ->
                    response.contentType = "application/json;charset=UTF-8"
                    response.status = 401
                    response.writer.write(
                        Ut.json.toString(
                            RsData("401-1", "사용자 인증정보가 올바르지 않습니다.", Empty())
                        )
                    )
                }

                accessDeniedHandler = AccessDeniedHandler { request, response, accessDeniedException ->
                    response.contentType = "application/json;charset=UTF-8"
                    response.status = 403
                    response.writer.write(
                        Ut.json.toString(
                            RsData("403-1", "권한이 없습니다.", Empty())
                        )
                    )
                }
            }
        }

        return http.build()
    }
}