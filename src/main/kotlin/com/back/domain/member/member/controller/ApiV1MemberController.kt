package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberDto
import com.back.domain.member.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/members")
class ApiV1MemberController(
    private val memberService: MemberService
) {
    @GetMapping("/me")
    fun me(req: HttpServletRequest): RsData<MemberDto> {
        val authorization = req.getHeader(HttpHeaders.AUTHORIZATION) ?: ""

        if (authorization.isBlank())
            throw ServiceException("401-1", "apiKey가 필요합니다.")

        if (!authorization.startsWith("Bearer "))
            throw ServiceException("401-2", "인증정보는 'Bearer [token]' 형태여야 합니다.")

        val apiKey = authorization.substring("Bearer ".length)

        if (apiKey.isBlank())
            throw ServiceException("401-1", "apiKey가 필요합니다.")

        val member = memberService
            .findByApiKey(apiKey)

        if (member == null)
            throw ServiceException("401-3", "apiKey가 올바르지 않습니다.")

        return RsData(
            resultCode = "200-1",
            msg = "OK",
            data = MemberDto(member)
        )
    }


    class MemberLoginReqBody(
        @field:NotBlank
        val username: String,
        @field:NotBlank
        val password: String
    )

    class MemberLoginResBody(
        val item: MemberDto,
        val apiKey: String
    )

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid reqBody: MemberLoginReqBody
    ): RsData<MemberLoginResBody> {
        val member = memberService.findByUsername(reqBody.username)
            ?: throw ServiceException("400-1", "존재하지 않는 회원입니다.")

        if (member.password != reqBody.password) {
            throw ServiceException("400-2", "비밀번호가 일치하지 않습니다.")
        }

        return RsData(
            resultCode = "200-1",
            msg = "${member.nickname}님 환영합니다.",
            data = MemberLoginResBody(
                item = MemberDto(member),
                apiKey = member.apiKey
            )
        )
    }
}