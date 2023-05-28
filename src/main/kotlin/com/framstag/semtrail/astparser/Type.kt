package com.framstag.semtrail.astparser

interface Type {
    fun isFunctionCall(): Boolean {
        return false
    }

    fun toFunctionCall(): FunctionCall {
        throw InternalError()
    }

    fun isList(): Boolean {
        return false
    }

    fun toList(): List {
        throw InternalError()
    }

    fun isSymbol(): Boolean {
        return false
    }

    fun toSymbol(): Symbol {
        throw InternalError()
    }

    fun isAtom(): Boolean {
        return false
    }

    fun toAtom(): Atom {
        throw InternalError()
    }

    fun isText(): Boolean {
        return false
    }

    fun toText(): Text {
        throw InternalError()
    }


    fun isMap(): Boolean {
        return false
    }

    fun toMap(): Map {
        throw InternalError()
    }
}
