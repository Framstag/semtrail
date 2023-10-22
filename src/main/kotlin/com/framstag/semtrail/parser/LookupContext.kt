package com.framstag.semtrail.parser

import kotlin.collections.List

class LookupContext(private val parent: LookupContext? = null) {
    private val map = mutableMapOf<String,MutableList<FunctionDefinition>>()

    fun addFunction(definition: FunctionDefinition) {
        if (!map.contains(definition.name)) {
            map[definition.name] = mutableListOf(definition);
        }
        else {
            map[definition.name]?.add(definition)
        }
    }

    fun getFunction(name: String):List<FunctionDefinition> {
        val functionDefinition = map[name]

        if (functionDefinition != null) {
            return functionDefinition
        }

        return parent?.getFunction(name) ?: listOf()
    }
}