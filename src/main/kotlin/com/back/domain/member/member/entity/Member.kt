package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class Member(
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false)
    var password: String,
    @Column(nullable = false)
    var nickname: String,
    var apiKey: String
) : BaseTime() {
    override fun toString(): String {
        return "Member(id=$id, username='$username', password='$password', name='$nickname')"
    }
}