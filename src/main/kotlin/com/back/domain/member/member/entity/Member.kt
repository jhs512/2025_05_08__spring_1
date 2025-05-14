package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Member(
    id: Long = 0,
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false)
    var password: String,
    @Column(nullable = false)
    var nickname: String,
    var apiKey: String
) : BaseTime(id) {
    override fun toString(): String {
        return "Member(id=$id, username='$username', password='$password', name='$nickname')"
    }

    val authoritiesAsStringList: List<String>
        get() {
            val authorities: MutableList<String> = ArrayList()

            if (isAdmin) authorities.add("ROLE_ADMIN")

            return authorities
        }

    val authorities: Collection<GrantedAuthority>
        get() = authoritiesAsStringList
            .stream()
            .map { SimpleGrantedAuthority(it) }
            .toList()

    val isAdmin: Boolean
        get() = "admin" == username || "system" == username
}