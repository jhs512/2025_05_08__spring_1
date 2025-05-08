package com.back.domain.member.member.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun count(): Long {
        return memberRepository.count()
    }

    fun join(username: String, password: String, name: String): Member {
        val member = Member(
            username = username,
            password = password,
            name = name
        )

        return memberRepository.save(member)
    }

    fun findByUsername(username: String): Member? {
        return memberRepository.findByUsername(username)
    }
}