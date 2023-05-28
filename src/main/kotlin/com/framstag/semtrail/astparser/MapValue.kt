package com.framstag.semtrail.astparser

class MapValue(val value: kotlin.collections.Map<StringValue,Value>):Value {

    override fun isMapValue(): Boolean {
        return true
    }

    override fun toMapValue(): MapValue {
        return this
    }
}