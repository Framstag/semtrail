package com.framstag.semtrail.astparser

import mu.KLogging
import java.util.Vector

class Executor {
    companion object : KLogging()

    fun evaluate(code: Type, lookupContext: LookupContext): Value {
        return when {
            code.isFunctionCall() -> {
                evaluateFunction(code.toFunctionCall(), lookupContext)
            }

            code.isAtom() -> {
                StringValue(code.toAtom().value())
            }

            code.isSymbol() -> {
                StringValue(code.toSymbol().value())
            }

            code.isText() -> {
                StringValue(code.toText().value())
            }

            code.isList() -> {
                evaluateList(code.toList(),lookupContext)
            }

            code.isMap() -> {
                evaluateMap(code.toMap(),lookupContext)
            }

            else -> NilValue.NIL
        }
    }

    private fun evaluateFunction(call: FunctionCall, lookupContext: LookupContext):Value {
        val parameterIter = call.getParameter().listIterator()
        val functionNameType = parameterIter.next();

        if (!functionNameType.isSymbol()) {
            logger.error("Function name must be of type 'Symbol'")
        }

        val functionName = functionNameType.toSymbol().value()
        val function = lookupContext.getFunction(functionName)

        if (function == null) {
            logger.error("No function '${functionName}' defined")
            return NilValue.NIL
        }

        val functionIsVariadic = function.variadic
        val functionParameterSizeMatches = call.getParameter().size-1 == function.parameterCount
        val functionMinimumParameterAsDefined = call.getParameter().size-1 >= function.parameterCount

        if ((!functionIsVariadic && !functionParameterSizeMatches) ||
            (functionIsVariadic && !functionMinimumParameterAsDefined)
        ) {
            logger.error("Function $functionName has arity ${function.parameterCount} and variadic $functionIsVariadic, but parameter count is ${call.getParameter().size - 1}")
            return NilValue.NIL
        }

        val parameterArray = Vector<Value>(call.getParameter().size-1)

        val functionContext = function.subContext ?: lookupContext

        while (parameterIter.hasNext()) {
            val parameter = parameterIter.next()
            parameterArray.add(evaluate(parameter,functionContext))
        }

        return function.callback(parameterArray)
    }

    private fun evaluateList(list : List, lookupContext: LookupContext):Value {
        val resultList: MutableList<Value> = mutableListOf()

        list.getMember().forEach {
            resultList.add(evaluate(it,lookupContext))
        }

        return ListValue(resultList)
    }

    private fun evaluateMap(map : Map, lookupContext: LookupContext):Value {
        val resultMap: MutableMap<StringValue,Value> = mutableMapOf()

        map.value().entries.forEach {
            val keyValue = evaluate(it.key,lookupContext)
            val valueValue = evaluate(it.value,lookupContext)

            if (!keyValue.isStringValue()) {
                logger.error("key value of map is not of type 'String'")
                return NilValue.NIL
            }

            resultMap[keyValue.toStringValue()] = valueValue
        }

        return MapValue(resultMap)
    }
}