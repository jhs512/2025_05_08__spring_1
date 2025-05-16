package com.back.domain.member.member.controller

import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
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
            .andExpect(jsonPath("$.data.apiKey").value(member.apiKey))
            .andExpect(jsonPath("$.data.item").exists())
            .andExpect(jsonPath("$.data.item.id").value(member.id))
            .andExpect(
                jsonPath("$.data.item.createDate")
                    .value(startsWith(member.createDate.toString().substring(0, 20)))
            )
            .andExpect(
                jsonPath("$.data.item.modifyDate")
                    .value(startsWith(member.modifyDate.toString().substring(0, 20)))
            )
            .andExpect(jsonPath("$.data.item.nickname").value(member.nickname))
    }

    @Test
    @DisplayName("로그인, with wrong endpoint")
    fun t2() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login-wrong")
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

        resultActions
            .andExpect(status().isUnauthorized()) // 로그인을 안한 상태라면 잘못된 엔트포인트에 접근했을 때 404 대신 401이 먼저 발생하게 된다.
            .andExpect(jsonPath("$.resultCode").value("401-1"))
            .andExpect(jsonPath("$.msg").value(containsString("사용자 인증정보가 올바르지 않습니다.")))
    }

    @Test
    @DisplayName("로그인 with empty data")
    fun t3() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login")
                    .content(
                        """
                        {
                            "username": "",
                            "password": ""
                        }
                        """.trimIndent()
                    )
                    .contentType(
                        MediaType(APPLICATION_JSON, UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.resultCode").value("400-1"))
            .andExpect(
                jsonPath(
                    "$.msg",
                    containsString("password-NotBlank-must not be blank")
                )
            )
            .andExpect(
                jsonPath(
                    "$.msg",
                    containsString("username-NotBlank-must not be blank")
                )
            )
    }

    @Test
    @DisplayName("로그인 with wrong username")
    fun t4() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login")
                    .content(
                        """
                        {
                            "username": "user0",
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
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.resultCode").value("400-1"))
            .andExpect(
                jsonPath(
                    "$.msg",
                    containsString("존재하지 않는 회원입니다.")
                )
            )
    }

    @Test
    @DisplayName("로그인, with no username")
    fun t5() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login")
                    .content(
                        """
                        {
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
            .andExpect(jsonPath("$.msg").value(containsString("잘못된 요청입니다.")))
            .andExpect(jsonPath("$.msg").value(containsString("parameter username which is a non-nullable type")))
    }

    @Test
    @DisplayName("로그인 with empty username")
    fun t6() {
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

    @Test
    @DisplayName("로그인 with wrong password")
    fun t7() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login")
                    .content(
                        """
                        {
                            "username": "user1",
                            "password": "wrong-password"
                        }
                        """.trimIndent()
                    )
                    .contentType(
                        MediaType(APPLICATION_JSON, UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.resultCode").value("400-2")).andExpect(
                jsonPath(
                    "$.msg",
                    containsString("비밀번호가 일치하지 않습니다.")
                )
            )
    }

    @Test
    @DisplayName("로그인 with no password")
    fun t8() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login")
                    .content(
                        """
                        {
                            "username": "user1"
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
            .andExpect(jsonPath("$.msg").value(containsString("잘못된 요청입니다.")))
            .andExpect(jsonPath("$.msg").value(containsString("parameter password which is a non-nullable type")))
    }

    @Test
    @DisplayName("로그인 with empty password")
    fun t9() {
        val resultActions = mvc
            .perform(
                post("/api/v1/members/login")
                    .content(
                        """
                        {
                            "username": "user1",
                            "password": ""
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
            .andExpect(jsonPath("$.msg").value("password-NotBlank-must not be blank"))
    }

    @Test
    @DisplayName("내 정보")
    @WithUserDetails("user1")
    fun t10() {
        val resultActions = mvc
            .perform(
                get("/api/v1/members/me")
            )
            .andDo(MockMvcResultHandlers.print())

        val member = memberService.findByUsername("user1").getOrThrow()

        resultActions
            .andExpect(handler().handlerType(ApiV1MemberController::class.java))
            .andExpect(handler().methodName("me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("200-1"))
            .andExpect(jsonPath("$.msg").value("OK"))
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
    @DisplayName("내 정보, with no authorization header")
    fun t11() {
        val resultActions = mvc
            .perform(
                get("/api/v1/members/me")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.resultCode").value("401-1"))
            .andExpect(jsonPath("$.msg").value("사용자 인증정보가 올바르지 않습니다."))
    }

    @Test
    @DisplayName("내 정보, doesn't start with Bearer")
    fun t12() {
        val resultActions = mvc
            .perform(
                get("/api/v1/members/me")
                    .header(HttpHeaders.AUTHORIZATION, "user1 access-token")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.resultCode").value("401-1"))
            .andExpect(jsonPath("$.msg").value("사용자 인증정보가 올바르지 않습니다."))
    }

    @Test
    @DisplayName("내 정보, with wrong apiKey")
    fun t13() {
        val resultActions = mvc
            .perform(
                get("/api/v1/members/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer wrong-api-key wrong-access-token")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.resultCode").value("401-1"))
            .andExpect(jsonPath("$.msg").value("사용자 인증정보가 올바르지 않습니다."))
    }

    @Test
    @DisplayName("내 정보, with wrong endpoint")
    @WithUserDetails("user1")
    fun t14() {
        val resultActions = mvc
            .perform(
                get("/api/v1/members/me-wrong")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.resultCode").value("404-1"))
            .andExpect(jsonPath("$.msg").value(containsString("해당 엔드포인트는 존재하지 않습니다.")))
    }

    @Test
    @DisplayName("로그아웃")
    fun t15() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.delete("/api/v1/members/logout")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("로그아웃 되었습니다."))
            .andExpect { result: MvcResult ->
                val accessTokenCookie = result.response.getCookie("accessToken")!!
                Assertions.assertThat(accessTokenCookie.value).isEmpty()
                Assertions.assertThat(accessTokenCookie.maxAge).isEqualTo(0)
                Assertions.assertThat(accessTokenCookie.path).isEqualTo("/")
                Assertions.assertThat(accessTokenCookie.isHttpOnly).isTrue()
                Assertions.assertThat(accessTokenCookie.secure).isTrue()

                val apiKeyCookie = result.response.getCookie("apiKey")!!
                Assertions.assertThat(apiKeyCookie.value).isEmpty()
                Assertions.assertThat(apiKeyCookie.maxAge).isEqualTo(0)
                Assertions.assertThat(apiKeyCookie.path).isEqualTo("/")
                Assertions.assertThat(apiKeyCookie.isHttpOnly).isTrue()
                Assertions.assertThat(apiKeyCookie.secure).isTrue()
            }
    }
}