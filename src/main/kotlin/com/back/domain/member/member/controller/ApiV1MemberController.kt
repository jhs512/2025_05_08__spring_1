package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberDto
import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}