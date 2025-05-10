package com.back.domain.member.member.dto

import com.back.domain.member.member.entity.Member
import java.time.LocalDateTime

class MemberDto(
    val id: Long,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val nickname: String,
) {
    constructor(member: Member) : this(
        id = member.id,
        createDate = member.createDate,
        modifyDate = member.modifyDate,
        nickname = member.nickname,
    )
}