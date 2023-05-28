package com.framstag.semtrail.astparser

class IntegerValue : Value {
    override fun isIntegerValue(): Boolean {
        return true
    }

    override fun toIntegerValue(): IntegerValue {
        return this
    }
}