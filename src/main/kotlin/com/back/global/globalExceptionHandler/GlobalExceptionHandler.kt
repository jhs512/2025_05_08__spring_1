package com.back.global.globalExceptionHandler

import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import com.back.standard.base.Empty
import com.back.standard.extensions.getOrDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

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

    @ExceptionHandler(NoResourceFoundException::class)
    fun handle(ex: NoResourceFoundException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                RsData(
                    "404-1",
                    "해당 엔드포인트는 존재하지 않습니다." + ex.message
                        ?.let { " : $it" }
                        .getOrDefault("")
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ResponseEntity<RsData<Empty>> {
        val message = ex.bindingResult
            .allErrors
            .filter { it is FieldError }
            .map { it as FieldError }
            .map { it.field + "-" + it.code + "-" + it.defaultMessage }
            .sorted()
            .joinToString("\n")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                RsData(
                    "400-1",
                    message
                )
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                RsData(
                    "400-1",
                    "잘못된 요청입니다." + ex.message
                        ?.let { " : $it" }
                        .getOrDefault("")
                )
            )
    }

    @ExceptionHandler(
        ServiceException::class
    )
    fun handle(ex: ServiceException): ResponseEntity<RsData<Empty>> {
        val rsData = ex.rsData

        return ResponseEntity
            .status(rsData.statusCode)
            .body(rsData)
    }
}