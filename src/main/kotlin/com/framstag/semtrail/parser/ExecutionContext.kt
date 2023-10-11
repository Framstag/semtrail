package com.framstag.semtrail.parser

import com.framstag.semtrail.model.ModelBuilder
import java.util.*

class ExecutionContext {

    fun assertStringParameter(values: Vector<Value>, index: Int, functionName: String, parameterName: String): String? {
        if (!values[index].isStringValue()) {
            ModelBuilder.logger.error("Expected type 'String' for parameter '$parameterName' at index ${index + 1} of function '$functionName'")
            return null
        }

        return values[index].toStringValue().value
    }
}