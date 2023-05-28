package com.framstag.semtrail.astparser

interface Value {
    fun isStringValue():Boolean {
        return false
    }

    fun toStringValue():StringValue {
        throw InternalError()
    }

    fun isIntegerValue():Boolean {
        return false
    }

    fun toIntegerValue():IntegerValue {
        throw InternalError()
    }

    fun isListValue():Boolean {
        return false
    }

    fun toListValue():ListValue {
        throw InternalError()
    }

    fun isMapValue():Boolean {
        return false
    }

    fun toMapValue():MapValue {
        throw InternalError()
    }

    fun isNilValue():Boolean {
        return false
    }

    fun toNilValue():NilValue {
        throw InternalError()
    }
}