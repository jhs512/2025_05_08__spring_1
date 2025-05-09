package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberWithUsernameDto
import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import com.back.standard.search.MemberSearchKeywordTypeV1
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/adm/members")
class ApiV1AdmMemberController(
    private val memberService: MemberService
) {
    @GetMapping
    fun items(
        @RequestParam(defaultValue = "all") searchKeywordType: MemberSearchKeywordTypeV1,
        @RequestParam(defaultValue = "") searchKeyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "30") pageSize: Int
    ): Page<MemberWithUsernameDto> {
        return memberService.search(searchKeywordType, searchKeyword, page, pageSize)
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