package com.back.domain.member.member.controller

import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import org.hamcrest.Matchers.startsWith
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets.UTF_8

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc // mockMvc 변수 주입 받을 수 있도록 세팅
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
            .andExpect(handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(handler().methodName("login"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("200-1"))
            .andExpect(jsonPath("$.msg").value("${member.nickname}님 환영합니다."))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.id").value(member.id))
            .andExpect(
                jsonPath("$.data.createDate")
                    .value(startsWith(member.createDate.toString().substring(0, 20)))
            )
            .andExpect(
                jsonPath("$.data.modifyDate")
                    .value(startsWith(member.modifyDate.toString().substring(0, 20)))
            )
            .andExpect(jsonPath("$.data.nickname").value(member.nickname))
    }

    @Test
    @DisplayName("로그인, without username")
    fun t2() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login")
                    .content(
                        """
                        {
                            "username": "",
                            "password": "1234"
                        }
                        """.trimIndent()
                    )
                    .contentType(
                        MediaType(APPLICATION_JSON, UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(handler().methodName("login"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.resultCode").value("400-1"))
            .andExpect(jsonPath("$.msg").value("username-NotBlank-must not be blank"))
    }
}