package com.back.standard.extensions


fun <T : Any> T?.getOrThrow(): T {
    return this ?: throw NoSuchElementException()
}

fun <T : Any> T?.getOrThrow(exception: Exception): T {
    return this ?: throw exception
}

fun <T : Any> T?.getOrDefault(default: T): T {
    return this ?: default
}
