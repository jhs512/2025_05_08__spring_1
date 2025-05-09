package com.back.standard.util

object Ut {
    object str {
        @JvmStatic
        fun isBlank(str: String?): Boolean {
            return str == null || str.trim().isEmpty()
        }

        @JvmStatic
        fun lcfirst(str: String): String {
            return str[0].lowercaseChar() + str.substring(1)
        }

        @JvmStatic
        fun isNotBlank(str: String?): Boolean {
            return !isBlank(str)
        }
    }
}