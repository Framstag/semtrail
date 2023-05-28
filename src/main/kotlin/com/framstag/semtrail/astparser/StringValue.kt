package com.framstag.semtrail.astparser

class StringValue(val value:String):Value {
    override fun isStringValue(): Boolean {
        return true
    }

    override fun toStringValue(): StringValue {
        return this
    }
}