package com.framstag.semtrail.parser

class LookupContext(private val parent: LookupContext? = null) {
    private val map = mutableMapOf<String,FunctionDefinition>()

    fun addFunction(definition: FunctionDefinition) {
        map[definition.name]=definition
    }

    fun getFunction(name: String):FunctionDefinition? {
        val functionDefinition = map[name]

        if (functionDefinition != null) {
            return functionDefinition
        }

        return parent?.getFunction(name)
    }
}