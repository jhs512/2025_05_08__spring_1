package com.back.domain.member.member.controller

import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets.UTF_8

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional // 테스트 환경에서의 @Transactional 는 @Transactional + @Rollback(true) 와 동일하다.
class ApiV1MemberControllerTest {
    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @DisplayName("로그인")
    fun t1() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login")
                    .content(
                        """
                        {
                            "username": "user1",
                            "password": "1234"
                        }
                        """.trimIndent()
                    )
                    .contentType(
                        MediaType(APPLICATION_JSON, UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        val member = memberService.findByUsername("user1").getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${member.nickname}님 환영합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(member.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.createDate")
                    .value(Matchers.startsWith(member.createDate.toString().substring(0, 20)))
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.modifyDate")
                    .value(Matchers.startsWith(member.modifyDate.toString().substring(0, 20)))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value(member.nickname))
    }
}