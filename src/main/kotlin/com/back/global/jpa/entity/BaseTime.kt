package com.back.global.jpa.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BaseTime(
    id: Long = 0
) : BaseEntity(id) {
    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var modifyDate: LocalDateTime
}