package com.back.global.security

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.service.MemberService
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import com.back.standard.base.Empty
import com.back.standard.util.Ut
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
    private data class AuthTokens(val apiKey: String, val accessToken: String)

    private fun getAuthTokensFromRequest(): AuthTokens? {
        val authorization = rq.getHeader(HttpHeaders.AUTHORIZATION, "")

        if (!authorization.isNullOrEmpty() && authorization.startsWith("Bearer ")) {
            val token = authorization.removePrefix("Bearer ")
            val tokenBits = token.split(" ", limit = 2)

            if (tokenBits.size == 2) {
                return AuthTokens(tokenBits[0], tokenBits[1])
            }
        }

        val apiKey = rq.getCookieValue("apiKey")
        val accessToken = rq.getCookieValue("accessToken")

        return if (!apiKey.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
            AuthTokens(apiKey, accessToken)
        } else {
            null
        }
    }

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

        val authTokens = getAuthTokensFromRequest()

        if (authTokens == null) {
            filterChain.doFilter(request, response)
            return
        }

        val (apiKey, accessToken) = authTokens

        var member = memberService.getMemberFromAccessToken(accessToken)

        if (member == null) {
            member = refreshAccessTokenByApiKey(apiKey)
        }

        if (member == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write(
                Ut.json.toString(
                    RsData(
                        "401-1",
                        "사용자 인증정보가 올바르지 않습니다.",
                        Empty()
                    )
                )
            )

            return
        }

        rq.setLogin(member)

        filterChain.doFilter(request, response)
    }

    private fun refreshAccessTokenByApiKey(apiKey: String): Member? {
        val member = memberService.findByApiKey(apiKey) ?: return null
        refreshAccessToken(member)
        return member
    }

    private fun refreshAccessToken(member: Member) {
        rq.refreshAccessToken(member)
    }
}