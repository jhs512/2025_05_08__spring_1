package com.back.global.initData

import com.back.domain.member.member.service.MemberService
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NotProdInitData(
    private val memberService: MemberService
) {
    @Bean
    fun notProdInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            if (memberService.count() > 0) {
                return@ApplicationRunner
            }

            val memberSystem = memberService.join(
                "system", "1234", "시스템"
            )

            val memberAdmin = memberService.join(
                "admin", "1234", "관리자"
            )

            val memberUser1 = memberService.join(
                "user1", "1234", "유저1"
            )

            val memberUser2 = memberService.join(
                "user2", "1234", "유저2"
            )

            val memberUser3 = memberService.join(
                "user3", "1234", "유저3"
            )

            listOf(
                memberSystem,
                memberAdmin,
                memberUser1,
                memberUser2,
                memberUser3
            ).forEach {
                println(it)
            }
        }
    }
}
