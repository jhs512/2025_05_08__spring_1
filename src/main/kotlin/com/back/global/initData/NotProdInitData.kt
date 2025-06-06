package com.back.global.initData

import com.back.domain.member.member.repository.MemberRepository
import com.back.domain.member.member.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional

@Configuration
class NotProdInitData(
    private val memberService: MemberService
) {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    @Lazy
    lateinit var self: NotProdInitData

    @Bean
    fun notProdInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            self.work1()

            self.work2()
        }
    }

    @Transactional
    fun work1() {
        if (memberService.count() > 0) return

        val memberSystem = memberService.join(
            "system", "1234", "시스템"
        )
        memberSystem.apiKey = "system"

        val memberAdmin = memberService.join(
            "admin", "1234", "관리자"
        )
        memberAdmin.apiKey = "admin"

        val memberUser1 = memberService.join(
            "user1", "1234", "유저1"
        )
        memberUser1.apiKey = "user1"

        val memberUser2 = memberService.join(
            "user2", "1234", "유저2"
        )
        memberUser2.apiKey = "user2"

        val memberUser3 = memberService.join(
            "user3", "1234", "유저3"
        )
        memberUser3.apiKey = "user3"
    }

    @Transactional
    fun work2() {
        memberService.findByUsername("user3")?.let {
            it.nickname = "유저3-변경"
        }
    }
}
