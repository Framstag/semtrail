package com.framstag.semtrail.parser

import com.framstag.semtrail.dsl.ParserCallback
import java.util.*
import kotlin.collections.List
import kotlin.collections.Map

class ExecutionContext {

    fun assertStringParameter(values: Vector<Value>, index: Int, functionName: String, parameterName: String): String? {
        if (!values[index].isStringValue()) {
            ParserCallback.logger.error("Expected type 'String' for parameter '$parameterName' at index ${index + 1} of function '$functionName'")
            return null
        }

        return values[index].toStringValue().value
    }

    fun assertListParameter(values: Vector<Value>, index: Int, functionName: String, parameterName: String): List<Value>? {
        if (!values[index].isListValue()) {
            ParserCallback.logger.error("Expected type 'Map' for parameter '$parameterName' at index ${index + 1} of function '$functionName'")
            return null
        }

        return values[index].toListValue().value
    }

    fun assertMapParameter(values: Vector<Value>, index: Int, functionName: String, parameterName: String): Map<StringValue, Value>? {
        if (!values[index].isMapValue()) {
            ParserCallback.logger.error("Expected type 'Map' for parameter '$parameterName' at index ${index + 1} of function '$functionName'")
            return null
        }

        return values[index].toMapValue().value
    }

    fun assertMapParameterOrEmpty(values: Vector<Value>, index: Int, functionName: String, parameterName: String): Map<StringValue, Value>? {
        if (index>=values.size) {
            return  mapOf<StringValue,Value>()
        }
        else if (!values[index].isMapValue()) {
            ParserCallback.logger.error("Expected type 'Map' for parameter '$parameterName' at index ${index + 1} of function '$functionName'")
            return null
        }

        return values[index].toMapValue().value
    }
}