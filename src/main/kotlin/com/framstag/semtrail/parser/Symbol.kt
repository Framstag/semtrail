package com.framstag.semtrail.parser

import com.framstag.semtrail.Token
import mu.KLogging

class Symbol(token: Token) : Type {
    companion object : KLogging()

    private val value:String

    init {
        value = token.value
    }

    override fun isSymbol(): Boolean {
        return true
    }

    override fun toSymbol(): Symbol {
        return this
    }

    fun value():String {
        return value
    }
}
