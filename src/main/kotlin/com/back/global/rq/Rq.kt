package com.back.global.rq

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.security.SecurityUser
import jakarta.servlet.http.HttpServletRequest
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
    val req: HttpServletRequest
) {
    fun getHeader(name: String, defaultValue: String): String {
        return req.getHeader(name) ?: defaultValue
    }

    val member: Member by lazy {
        SecurityContextHolder
            .getContext().authentication
            ?.principal
            ?.let { it as SecurityUser }
            ?.let { memberService.findById(it.id) }
            ?: throw ServiceException("401-1", "인증정보가 필요합니다.")
    }

    fun setLogin(member: Member) {
        val user = SecurityUser(
            id = member.id,
            member.username,
            password = "",
            member.nickname,
            authorities = listOf()
        )

        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            user,
            user.password,
            user.authorities
        )

        SecurityContextHolder.getContext().authentication = authentication
    }
}