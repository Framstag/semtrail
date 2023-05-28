package com.framstag.semtrail.astparser

import com.framstag.semtrail.Token
import mu.KLogging

class Atom(token: Token) : Type {
    companion object : KLogging()

    private val value: String

    init {
        value = token.value
    }

    override fun isAtom(): Boolean {
        return true
    }

    override fun toAtom(): Atom {
        return this
    }

    fun value():String {
        return value
    }
}
