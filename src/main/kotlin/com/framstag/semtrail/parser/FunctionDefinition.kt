package com.framstag.semtrail.parser

import java.util.*

class FunctionDefinition(
    val name: String,
    val callback: (context: ExecutionContext, values: Vector<Value>)-> Value,
    val parameterCount: Int = 0,
    val variadic:Boolean = false,
    val subContext: LookupContext? = null
)