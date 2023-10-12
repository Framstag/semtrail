package com.framstag.semtrail.parser

import com.framstag.semtrail.model.ModelBuilder
import java.util.*
import kotlin.collections.List
import kotlin.collections.Map

class ExecutionContext {

    fun assertStringParameter(values: Vector<Value>, index: Int, functionName: String, parameterName: String): String? {
        if (!values[index].isStringValue()) {
            ModelBuilder.logger.error("Expected type 'String' for parameter '$parameterName' at index ${index + 1} of function '$functionName'")
            return null
        }

        return values[index].toStringValue().value
    }

    fun assertListParameter(values: Vector<Value>, index: Int, functionName: String, parameterName: String): List<Value>? {
        if (!values[index].isListValue()) {
            ModelBuilder.logger.error("Expected type 'Map' for parameter '$parameterName' at index ${index + 1} of function '$functionName'")
            return null
        }

        return values[index].toListValue().value
    }

    fun assertMapParameter(values: Vector<Value>, index: Int, functionName: String, parameterName: String): Map<StringValue, Value>? {
        if (!values[index].isMapValue()) {
            ModelBuilder.logger.error("Expected type 'Map' for parameter '$parameterName' at index ${index + 1} of function '$functionName'")
            return null
        }

        return values[index].toMapValue().value
    }
}