package com.framstag.semtrail.parser

import com.framstag.semtrail.Token
import mu.KLogging

class Text(token: Token) : Type {
    companion object : KLogging()

    private val value : String

    init {
        value = token.value
    }

    override fun isText(): Boolean {
        return true
    }

    override fun toText(): Text {
        return this
    }

    fun value():String {
        return value
    }
}
