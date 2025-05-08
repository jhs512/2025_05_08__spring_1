package com.back.domain.member.member.entity

import jakarta.persistence.*

@Entity
class Member(
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false)
    var password: String,
    @Column(nullable = false)
    var name: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    override fun toString(): String {
        return "Member(id=$id, username='$username', password='$password', name='$name')"
    }
}