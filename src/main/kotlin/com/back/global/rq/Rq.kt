package com.back.global.rq

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.service.MemberService
import com.back.global.exception.ServiceException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
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
    val member: Member by lazy {
        // 최초 접근 시 한 번만 실행됨
        val authorization = req.getHeader(HttpHeaders.AUTHORIZATION) ?: ""
        if (authorization.isBlank())
            throw ServiceException("401-1", "apiKey가 필요합니다.")
        if (!authorization.startsWith("Bearer "))
            throw ServiceException("401-2", "인증정보는 'Bearer [token]' 형태여야 합니다.")
        val apiKey = authorization.substring("Bearer ".length)
        if (apiKey.isBlank())
            throw ServiceException("401-1", "apiKey가 필요합니다.")
        memberService.findByApiKey(apiKey)
            ?: throw ServiceException("401-3", "apiKey가 올바르지 않습니다.")
    }
}