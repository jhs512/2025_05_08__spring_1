package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUsername(username: String): Member?
    fun findAllByUsernameContaining(searchKeyword: String, pageable: Pageable): Page<Member>
    fun findAllByNicknameContaining(searchKeyword: String, pageable: Pageable): Page<Member>
    fun findAllByUsernameContainingOrNicknameContaining(
        searchKeyword1: String,
        searchKeyword2: String,
        pageable: Pageable
    ): Page<Member>
}