package com.framstag.semtrail

class Token(val type: TokenType,
            val value: String,
            val column: Int,
            val row: Int) {

    fun getRef():String {
        return if (value.isEmpty()) {
            type.toString()
        } else {
            "'$value' ($type)"
        }
    }

}