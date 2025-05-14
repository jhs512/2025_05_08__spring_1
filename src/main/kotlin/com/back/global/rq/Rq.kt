package com.back.global.rq

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.service.MemberService
import com.back.global.app.AppConfig
import com.back.global.exception.ServiceException
import com.back.global.security.SecurityUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
// Request
// 컨트롤러 수준에서 자주 사용되는 로직을 모아두는 곳
class Rq(
    private val memberService: MemberService,
    val req: HttpServletRequest,
    val resp: HttpServletResponse,
) {
    fun getHeader(name: String, defaultValue: String): String {
        return req.getHeader(name) ?: defaultValue
    }

    fun setHeader(name: String, value: String) {
        req.setAttribute(name, value)
    }

    fun getCookieValue(name: String): String? {
        return req.cookies?.find { it.name == name }?.value
    }

    private fun cookieDomain(): String {
        val domain = AppConfig.getSiteCookieDomain()

        if (domain == "localhost") return domain

        return ".$domain"
    }

    fun setCookie(name: String, value: String) {
        val cookie = ResponseCookie.from(name, value)
            .path("/")
            .domain(cookieDomain())
            .sameSite("Strict")
            .secure(true)
            .httpOnly(true)
            .build()

        resp.addHeader("Set-Cookie", cookie.toString())
    }

    val member: Member by lazy {
        SecurityContextHolder
            .getContext()
            .authentication
            ?.principal
            ?.let { it as SecurityUser }
            ?.let { Member(id = it.id, username = it.username, password = "", nickname = it.nickname, apiKey = "") }

            ?: throw ServiceException("401-1", "인증정보가 필요합니다.")
    }

    val fulfilledMember: Member by lazy {
        memberService.findById(member.id)
            ?: throw ServiceException("401-2", "존재하지 않는 회원입니다.")
    }

    fun setLogin(member: Member) {
        val user = SecurityUser(
            id = member.id,
            member.username,
            password = "",
            member.nickname,
            authorities = member.authorities
        )

        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            user,
            user.password,
            user.authorities
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

    fun refreshAccessToken(member: Member) {
        val newAccessToken = memberService.genAccessToken(member)
        setHeader(HttpHeaders.AUTHORIZATION, "Bearer ${member.apiKey} $newAccessToken")
        setCookie("accessToken", newAccessToken)
    }

    fun makeAuthCookies(member: Member): String {
        val accessToken = memberService.genAccessToken(member)
        setCookie("apiKey", member.apiKey)
        setCookie("accessToken", accessToken)
        return accessToken
    }
}