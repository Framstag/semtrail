package com.framstag.semtrail.astparser

import mu.KLogging

class FunctionCall : Type {
    companion object : KLogging()

    private val parameter: MutableList<Type> = mutableListOf()

    override fun isFunctionCall(): Boolean {
        return true
    }

    override fun toFunctionCall(): FunctionCall {
        return this
    }

    fun addParameter(type: Type) {
        parameter.add(type)
    }

    fun getParameter(): kotlin.collections.List<Type> {
        return parameter.toList()
    }
}
