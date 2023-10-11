package com.framstag.semtrail.parser

class ListValue(val value: kotlin.collections.List<Value>):Value {

    override fun isListValue(): Boolean {
        return true
    }

    override fun toListValue(): ListValue {
        return this
    }
}