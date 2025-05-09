package com.back.global.globalExceptionHandler

import com.back.global.rsData.RsData
import com.back.standard.base.Empty
import com.back.standard.extensions.getOrDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException::class)
    fun handle(ex: NoSuchElementException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                RsData(
                    "404-1",
                    "data not found" + ex.message
                        ?.let { " : $it" }
                        .getOrDefault("")
                )
            )
    }
}