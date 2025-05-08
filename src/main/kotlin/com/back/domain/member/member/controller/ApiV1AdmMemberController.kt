package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/adm/members")
class ApiV1AdmMemberController {
    @GetMapping
    fun items(): List<MemberDto> {
        return listOf(
            MemberDto(1, "John Doe"),
            MemberDto(2, "Sarah Connor"),
            MemberDto(3, "Sam Smith")
        )
    }
}