package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class ApiV1MemberController {
    @GetMapping("/me")
    fun me(): MemberDto {
        val memberDto = MemberDto(
            id = 1L,
            name = "John Doe"
        )

        return memberDto
    }
}