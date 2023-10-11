package com.framstag.semtrail.parser

class NilValue private constructor() : Value {
    companion object {
        val NIL = NilValue()
    }

    override fun isNilValue(): Boolean {
        return true
    }

    override fun toNilValue(): NilValue {
        return NIL
    }
}