package com.back.global.security

import com.back.domain.member.member.service.MemberService
import com.back.global.rq.Rq
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

// 이 필터는 우리가 정한 인증정보를 스프링 시큐리티가 이해할 수 있는 형태의 인증정보로 변환하는 역할
// 이 필터는 모든 요청에 대해서 선제적으로 실행됨
@Component
class CustomAuthenticationFilter(
    private val memberService: MemberService,
    private val rq: Rq
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // API 요청이 아닌 경우 패스
        if (!request.requestURI.startsWith("/api/")) {
            filterChain.doFilter(request, response)
            return
        }

        // 로그인 여부와 전혀 관계없는 엔드포인트들이면 패스
        if (request.requestURI in listOf("/api/v1/members/login", "/api/v1/members/logout", "/api/v1/members/join")) {
            filterChain.doFilter(request, response)
            return
        }

        val authorization = rq.getHeader(HttpHeaders.AUTHORIZATION, "")

        // 인증정보가 없는 경우 패스
        if (authorization.isBlank() ) {
            filterChain.doFilter(request, response)
            return
        }

        // 인증정보가 Bearer 로 시작하지 않는 경우 패스
        if (!authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val apiKey = authorization.substring("Bearer ".length)

        // apiKey 가 비어있는 경우 패스
        if (apiKey.isBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        val member = memberService.findByApiKey(apiKey)
            ?: run {
                // apiKey 가 올바르지 않은 경우 패스
                filterChain.doFilter(request, response)
                return
            }

        // 시큐리티에게 현재 요청은 인증된 사용자의 요청이라는 것을 알림
        rq.setLogin(member)

        filterChain.doFilter(request, response)
    }
}