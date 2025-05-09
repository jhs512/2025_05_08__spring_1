package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberWithUsernameDto
import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/adm/members")
class ApiV1AdmMemberController(
    private val memberService: MemberService
) {
    @GetMapping
    fun items(
        @RequestParam(defaultValue = "") searchKeyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "30") pageSize: Int
    ): Page<MemberWithUsernameDto> {
        return memberService.findAllByUsernameContaining(searchKeyword, page, pageSize)
            .map { MemberWithUsernameDto(it) }
    }

    @GetMapping("/{id}")
    fun item(
        @PathVariable id: Long
    ): MemberWithUsernameDto {
        return memberService.findById(id)
            .getOrThrow()
            .let {
                MemberWithUsernameDto(it)
            }
    }
}