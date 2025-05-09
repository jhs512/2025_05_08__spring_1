package com.back.domain.member.member.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberRepository
import com.back.standard.search.MemberSearchKeywordTypeV1
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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

    fun findAll(): List<Member> {
        return memberRepository.findAll()
    }

    fun findById(id: Long): Member? {
        return memberRepository.findById(id)
            .orElse(null)
    }

    fun search(
        searchKeywordType: MemberSearchKeywordTypeV1,
        searchKeyword: String,
        page: Int,
        pageSize: Int
    ): Page<Member> {
        val pageable: Pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")))

        if (searchKeyword.isBlank()) {
            return memberRepository.findAll(pageable)
        }

        when (searchKeywordType) {
            MemberSearchKeywordTypeV1.all -> {
                return memberRepository.findAllByUsernameContainingOrNameContaining(
                    searchKeyword,
                    searchKeyword,
                    pageable
                )
            }

            MemberSearchKeywordTypeV1.username -> {
                return memberRepository.findAllByUsernameContaining(searchKeyword, pageable)
            }

            MemberSearchKeywordTypeV1.nickname -> {
                return memberRepository.findAllByNameContaining(searchKeyword, pageable)
            }
        }
    }
}