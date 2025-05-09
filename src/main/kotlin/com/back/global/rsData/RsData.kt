package com.back.global.rsData

import com.back.standard.base.Empty
import com.fasterxml.jackson.annotation.JsonIgnore

data class RsData<T>(
    val resultCode: String,
    val msg: String,
    val data: T
) {
    constructor(resultCode: String, msg: String) : this(resultCode, msg, Empty() as T)

    @get:JsonIgnore
    val statusCode: Int
        get() = resultCode.split("-", limit = 2)[0].toInt()
}