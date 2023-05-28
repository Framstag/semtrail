package com.framstag.semtrail.astparser

class LookupContext(val parent: LookupContext? = null) {
    private val map = mutableMapOf<String,FunctionDefinition>()

    fun addFunction(definition: FunctionDefinition) {
        map[definition.name]=definition
    }

    fun getFunction(name: String):FunctionDefinition? {
        val functionDefinition = map[name]

        if (functionDefinition != null) {
            return functionDefinition
        }

        if (parent != null) {
            return parent.getFunction(name)
        }
        else {
            return null
        }
    }
}