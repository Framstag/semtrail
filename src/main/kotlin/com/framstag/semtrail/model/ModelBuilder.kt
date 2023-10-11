package com.framstag.semtrail.model

import com.framstag.semtrail.parser.ExecutionContext
import com.framstag.semtrail.parser.NilValue
import com.framstag.semtrail.parser.Value
import mu.KLogging
import java.util.*

class ModelBuilder(private val model: Model) {
    companion object : KLogging()

    fun onSemtrail(context: ExecutionContext, values: Vector<Value>): Value {
        if (!values[0].isStringValue()) {
            logger.error("Expected 'String' for 1. parameter 'name' of 'semtrail' function")
            return NilValue.NIL
        }

        val nameValue = values[0].toStringValue().value

        model.name=nameValue

        return NilValue.NIL
    }

    fun log(context: ExecutionContext, values: Vector<Value>):Value {
        val value = context.assertStringParameter(values,0,"log","value"))
        if (!values[0].isStringValue()) {
            logger.error("Expected String for 1. parameter 'value' of 'log' function")
            return NilValue.NIL
        }

        val value = values[0].toStringValue().value

        logger.info("log: $value")

        return NilValue.NIL
    }

    private fun onNodeTypes(value : Value) {
        if (!value.isListValue()) {
            logger.error("Expected List for value of config entry :nodeTypes")
            return
        }

        val types = value.toListValue().value

        types.forEach {
            if (!it.isStringValue()) {
                logger.error("List elements for config entry :nodeTypes must be of type string")
                return
            }

            model.nodeTypeSet.add(it.toStringValue().value)
        }
    }

    private fun onNodeTypeNames(value : Value) {
        if (!value.isMapValue()) {
            logger.error("Expected Map for value of config entry :nodeTypeNames")
            return
        }

        val names = value.toMapValue().value

        names.entries.forEach {
            if (!it.value.isStringValue()) {
                logger.error("Map values for config entry :nodeTypeNames must be of type string")
                return
            }

            model.nodeTypeNames[it.key.value]=it.value.toStringValue().value
        }
    }

    private fun onNodeTypeColors(value : Value) {
        if (!value.isMapValue()) {
            logger.error("Expected Map for value of config entry :nodeTypeColors")
            return
        }

        val colors = value.toMapValue().value

        colors.entries.forEach {
            if (!it.value.isStringValue()) {
                logger.error("Map values for config entry :nodeTypeColors must be of type string")
                return
            }

            model.nodeTypeColors[it.key.value]=it.value.toStringValue().value
        }
    }

    private fun onEdgeTypes(value : Value) {
        if (!value.isListValue()) {
            logger.error("Expected List for value of config entry :edgeTypes")
            return
        }

        val types = value.toListValue().value

        types.forEach {
            if (!it.isStringValue()) {
                logger.error("List elements for config entry :edgeTypes must be of type string")
                return
            }

            model.edgeTypeSet.add(it.toStringValue().value)
        }
    }

    private fun onEdgeTypeNames(value : Value) {
        if (!value.isMapValue()) {
            logger.error("Expected Map for value of config entry :edgeTypeNames")
            return
        }

        val names = value.toMapValue().value

        names.entries.forEach {
            if (!it.value.isStringValue()) {
                logger.error("Map values for config entry :edgeTypeNames must be of type string")
                return
            }

            model.edgeTypeNames[it.key.value]=it.value.toStringValue().value
        }
    }

    private fun onEdgeTypeColors(value : Value) {
        if (!value.isMapValue()) {
            logger.error("Expected Map for value of config entry :edgeTypeColors")
            return
        }

        val colors = value.toMapValue().value

        colors.entries.forEach {
            if (!it.value.isStringValue()) {
                logger.error("Map values for config entry :edgeTypeColors must be of type string")
                return
            }

            model.edgeTypeColors[it.key.value]=it.value.toStringValue().value
        }
    }

    fun onConfig(context: ExecutionContext, values: Vector<Value>): Value {
        if (!values[0].isMapValue()) {
            logger.error("Expected Map for 1. parameter 'propertyMap' of 'config' function")
            return NilValue.NIL
        }

        val propertyMap = values[0].toMapValue().value

        propertyMap.forEach { stringValue, value ->
            if (stringValue.value==":nodeTypes") {
                onNodeTypes(value)
            }
            else if (stringValue.value==":nodeTypeNames") {
                onNodeTypeNames(value)
            }
            else if (stringValue.value==":nodeTypeColors") {
                onNodeTypeColors(value)
            }
            else if (stringValue.value==":edgeTypes") {
                onEdgeTypes(value)
            }
            else if (stringValue.value==":edgeTypeNames") {
                onEdgeTypeNames(value)
            }
            else if (stringValue.value==":edgeTypeColors") {
                onEdgeTypeColors(value)
            }
            else {
                logger.error("Unknown key ${stringValue} 'propertyMap' of 'config' function")
            }
        }

        return NilValue.NIL
    }

    fun onNode(context: ExecutionContext, values: Vector<Value>): Value {
        if (!values[0].isStringValue()) {
            logger.error("Expected String for 1. parameter 'nodeName' of 'node' function")
            return NilValue.NIL
        }

        if (!values[1].isMapValue()) {
            logger.error("Expected Map for 2. parameter 'attributes' of 'node' function")
            return NilValue.NIL
        }

        val nodeName = values[0].toStringValue().value
        val attributes = values[1].toMapValue().value

        if (model.hasNode(nodeName)) {
            logger.error("Node '${nodeName}' already exists")
            return NilValue.NIL
        }

        model.getOrCreateNode(nodeName)

        val node = model.getNode(nodeName)

        attributes.entries.forEach {
            if (it.key.value == ":type") {
                if (!it.value.isStringValue()) {
                    logger.error("Value for key ':type' for argument 'attributes' of function 'node' must be of type 'String'")
                }

                node?.type = it.value.toStringValue().value
            }
            else if (it.key.value == ":doc") {
                if (!it.value.isStringValue()) {
                    logger.error("Value for key ':doc' for argument 'attributes' of function 'node' must be of type 'String'")
                }

                node?.doc = it.value.toStringValue().value
            }
            else {
                logger.error("Unknown key ${it.key.value} for parameter 'attributes' of 'node' function")
            }
        }

        return NilValue.NIL
    }

    fun onEdge(context: ExecutionContext, values: Vector<Value>): Value {
        if (!values[0].isStringValue()) {
            logger.error("Expected String for 1. parameter 'fromNodeName' of 'edge' function")
            return NilValue.NIL
        }

        if (!values[1].isStringValue()) {
            logger.error("Expected String for 2. parameter 'edgeName' of 'edge' function")
            return NilValue.NIL
        }

        if (!values[2].isStringValue()) {
            logger.error("Expected String for 3. parameter 'toNodeName' of 'edge' function")
            return NilValue.NIL
        }

        val fromNodeName = values[0].toStringValue().value
        val typeName = values[1].toStringValue().value
        val toNodeName = values[2].toStringValue().value

        val fromNode=model.getOrCreateNode(fromNodeName)
        val toNode= model.getOrCreateNode(toNodeName)

        if (fromNode.hasTargetNode(toNode)) {
            logger.error("From node '${fromNode.name}' already has an edge to node '${toNode.name}'")
            return NilValue.NIL
        }

        fromNode.addOutgoingEdge(Edge(fromNode,typeName,toNode))
        toNode.addIncomingEdge(Edge(fromNode,typeName,toNode))

        return NilValue.NIL
    }
}