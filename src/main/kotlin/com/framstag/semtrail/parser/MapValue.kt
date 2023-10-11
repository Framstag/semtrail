package com.framstag.semtrail.parser

class MapValue(val value: kotlin.collections.Map<StringValue,Value>):Value {

    override fun isMapValue(): Boolean {
        return true
    }

    override fun toMapValue(): MapValue {
        return this
    }
}