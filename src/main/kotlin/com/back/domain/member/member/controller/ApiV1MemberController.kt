package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberDto
import com.back.domain.member.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import com.back.standard.extensions.getOrThrow
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/members")
class ApiV1MemberController(
    private val memberService: MemberService
) {
    @GetMapping("/me")
    fun me(): MemberDto {
        return memberService
            .findByUsername("user1")
            .getOrThrow()
            .let {
                MemberDto(it)
            }
    }


    class MemberLoginReqBody(
        @field:NotBlank
        val username: String,
        @field:NotBlank
        val password: String
    )

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid reqBody: MemberLoginReqBody
    ): RsData<Map<String, Any>> {
        val member = memberService.findByUsername(reqBody.username)
            ?: throw ServiceException("400-1", "존재하지 않는 회원입니다.")

        if (member.password != reqBody.password) {
            throw ServiceException("400-2", "비밀번호가 일치하지 않습니다.")
        }

        return RsData(
            resultCode = "200-1",
            msg = "${member.nickname}님 환영합니다.",
            data = mapOf(
                "item" to MemberDto(member),
                "apiKey" to member.apiKey
            )
        )
    }
}