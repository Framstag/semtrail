package com.framstag.semtrail.dsl

import com.framstag.semtrail.configuration.Configuration
import com.framstag.semtrail.generator.*
import com.framstag.semtrail.model.Edge
import com.framstag.semtrail.model.EdgeType
import com.framstag.semtrail.model.Model
import com.framstag.semtrail.model.NodeType
import com.framstag.semtrail.parser.ExecutionContext
import com.framstag.semtrail.parser.NilValue
import com.framstag.semtrail.parser.Value
import mu.KLogging
import java.util.*

class ParserCallback(private val config: Configuration, private val model: Model) {
    companion object : KLogging()

    fun onSemtrail(context: ExecutionContext, values: Vector<Value>): Value {
        val name = context.assertStringParameter(values,0,"semtrail","name") ?: return NilValue.NIL

        model.name=name

        return NilValue.NIL
    }

    fun log(context: ExecutionContext, values: Vector<Value>):Value {
        val value = context.assertStringParameter(values,0,"log","value") ?: return NilValue.NIL

        logger.info("log: $value")

        return NilValue.NIL
    }

    fun onNodeType(context: ExecutionContext, values: Vector<Value>):Value {
        val type = context.assertStringParameter(values,0,"nodeType","type") ?: return NilValue.NIL
        val name = context.assertStringParameter(values,1,"nodeType","name") ?: return NilValue.NIL
        val color = context.assertStringParameter(values,2,"nodeType","color") ?: return NilValue.NIL

        if (model.nodeTypes.contains(type)) {
            logger.error("nodeType: type '${type}' is already defined")
            return NilValue.NIL
        }

        model.nodeTypes[type] = NodeType(type, name, color)

        return NilValue.NIL
    }

    fun onEdgeType(context: ExecutionContext, values: Vector<Value>):Value {
        val type = context.assertStringParameter(values,0,"edeType","type") ?: return NilValue.NIL
        val name = context.assertStringParameter(values,1,"edgeType","name") ?: return NilValue.NIL
        val color = context.assertStringParameter(values,2,"edgeType","color") ?: return NilValue.NIL

        if (model.edgeTypes.contains(type)) {
            logger.error("edgeType: type '${type}' is already defined")
            return NilValue.NIL
        }

        model.edgeTypes[type] = EdgeType(type, name, color)

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onConfig(context: ExecutionContext, values: Vector<Value>): Value {
        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onReport(context: ExecutionContext, values: Vector<Value>): Value {
        return NilValue.NIL
    }

    fun onNode(context: ExecutionContext, values: Vector<Value>): Value {
        val name = context.assertStringParameter(values,0,"node","name") ?: return NilValue.NIL
        val type = context.assertStringParameter(values,1,"node","type") ?: return NilValue.NIL
        val attributes = context.assertMapParameter(values,2,"node","attributes") ?: return NilValue.NIL

        if (model.hasNode(name)) {
            logger.error("Node '${name}' already exists")
            return NilValue.NIL
        }

        if (!model.nodeTypes.contains(type)) {
            logger.error("Type '${type}' for node '${name}' does not exist")
            return NilValue.NIL
        }

        val node = model.getOrCreateNode(name)

        node.type=type;

        attributes.entries.forEach {
            when (it.key.value) {
                ":doc" -> {
                    if (!it.value.isStringValue()) {
                        logger.error("Value for key ':doc' for argument 'attributes' of function 'node' must be of type 'String'")
                        return NilValue.NIL
                    }

                    node.doc = it.value.toStringValue().value
                }
                else -> {
                    logger.error("Unknown key ${it.key.value} for parameter 'attributes' of 'node' function")
                    return NilValue.NIL
                }
            }
        }

        return NilValue.NIL
    }

    fun onEdge(context: ExecutionContext, values: Vector<Value>): Value {
        val fromNodeName = context.assertStringParameter(values,0,"edge","fromNodeName") ?: return NilValue.NIL
        val type = context.assertStringParameter(values,1,"edge","type") ?: return NilValue.NIL
        val toNodeName = context.assertStringParameter(values,2,"edge","toNodeName") ?: return NilValue.NIL

        val fromNode=model.getOrCreateNode(fromNodeName)
        val toNode= model.getOrCreateNode(toNodeName)

        if (fromNode.hasTargetNode(toNode)) {
            logger.error("From node '${fromNode.name}' already has an edge to node '${toNode.name}'")
            return NilValue.NIL
        }

        fromNode.addOutgoingEdge(Edge(fromNode,type,toNode))
        toNode.addIncomingEdge(Edge(fromNode,type,toNode))

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAllNodesTable(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(AllNodesTableGenerator())

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAllNodesImage(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(AllNodesImageGenerator())

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onStarterTable(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(StarterTableGenerator())

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLeavesTable(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(LeavesTableGenerator())

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMostCausesTable(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(MostCausesTableGenerator())

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMostConsequencesTable(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(MostConsequencesTableGenerator())

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMostConnectedTable(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(MostConnectedTableGenerator())

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onOrphansTable(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(OrphansTableGenerator())

        return NilValue.NIL
    }

    @Suppress("UNUSED_PARAMETER")
    fun onNoDocTable(context: ExecutionContext, values: Vector<Value>): Value {
        config.generators.add(NoDocTableGenerator())

        return NilValue.NIL
    }

    fun onNodeTypeTable(context: ExecutionContext, values: Vector<Value>): Value {
        val nodeType = context.assertStringParameter(values,0,"nodeTypeTable","nodeType") ?: return NilValue.NIL

        if (!model.nodeTypes.contains(nodeType)) {
            logger.error("nodeTypeTable: type '${nodeType}' is unknown")
            return NilValue.NIL
        }

        config.generators.add(NodeTypeTableGenerator(nodeType))

        return NilValue.NIL
    }
}