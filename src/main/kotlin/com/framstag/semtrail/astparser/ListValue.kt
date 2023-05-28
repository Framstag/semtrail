package com.framstag.semtrail.astparser

class ListValue(val value: kotlin.collections.List<Value>):Value {

    override fun isListValue(): Boolean {
        return true
    }

    override fun toListValue(): ListValue {
        return this
    }
}