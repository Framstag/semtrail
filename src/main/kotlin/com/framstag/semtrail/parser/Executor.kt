package com.framstag.semtrail.parser

import mu.KLogging
import java.util.Vector

class Executor {
    companion object : KLogging()

    private var errorCount = 0;

    fun evaluate(code: Type, lookupContext: LookupContext):Value {
        val context = ExecutionContext()

        return evaluate(context, code, lookupContext)
    }

    fun hasErrors():Boolean {
        return errorCount >0
    }

    private fun evaluate(context: ExecutionContext, code: Type, lookupContext: LookupContext): Value {
        return when {
            code.isFunctionCall() -> {
                evaluateFunction(context, code.toFunctionCall(), lookupContext)
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

    private fun evaluateFunction(context: ExecutionContext, call: FunctionCall, lookupContext: LookupContext):Value {
        val parameterIter = call.getParameter().listIterator()
        val functionNameType = parameterIter.next()

        if (!functionNameType.isSymbol()) {
            logger.error("Function name must be of type 'Symbol'")
            errorCount++
            return NilValue.NIL
        }

        val functionName = functionNameType.toSymbol().value()
        val functions = lookupContext.getFunction(functionName)
        var function : FunctionDefinition? = null

        if (functions.isEmpty()) {
            logger.error("No function '${functionName}' defined")
            errorCount++
            return NilValue.NIL
        }

        for (possibleFunction in functions) {
            val functionIsVariadic = possibleFunction.variadic
            val functionParameterSizeMatches = call.getParameter().size-1 == possibleFunction.parameterCount
            val functionMinimumParameterAsDefined = call.getParameter().size-1 >= possibleFunction.parameterCount

            if ((!functionIsVariadic && functionParameterSizeMatches) ||
                (functionIsVariadic && functionMinimumParameterAsDefined)) {
                function = possibleFunction
                break
            }
        }

        if (function == null) {
            logger.error("No function definition for function '$functionName' found with arity ${call.getParameter().size-1}")
            errorCount++
            return NilValue.NIL
        }

        val parameterArray = Vector<Value>(call.getParameter().size-1)

        val functionContext = function.subContext ?: lookupContext

        while (parameterIter.hasNext()) {
            val parameter = parameterIter.next()
            parameterArray.add(evaluate(parameter,functionContext))
        }

        return function.callback(context, parameterArray)
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
                errorCount++
                return NilValue.NIL
            }

            resultMap[keyValue.toStringValue()] = valueValue
        }

        return MapValue(resultMap)
    }
}