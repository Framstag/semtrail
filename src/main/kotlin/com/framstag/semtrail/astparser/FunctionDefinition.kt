package com.framstag.semtrail.astparser

import java.util.*

class FunctionDefinition(
    val name: String,
    val callback: (values: Vector<Value>)-> Value,
    val parameterCount: Int = 0,
    val variadic:Boolean = false,
    val subContext: LookupContext? = null
)