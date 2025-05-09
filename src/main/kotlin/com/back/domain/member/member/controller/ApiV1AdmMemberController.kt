package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberDto
import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/adm/members")
class ApiV1AdmMemberController(
    private val memberService: MemberService
) {
    @GetMapping
    fun items(): List<MemberDto> {
        return memberService.findAll().map {
            MemberDto(it)
        }
    }

    @GetMapping("/{id}")
    fun item(
        @PathVariable id: Long
    ): MemberDto {
        return memberService.findById(id)
            .getOrThrow()
            .let {
                MemberDto(it)
            }
    }
}