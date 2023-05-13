package com.framstag.semtrail

import mu.KLogging

class ModelBuilder(private val model: Model) : ASTCallback {
    companion object : KLogging()

    override fun onSemtrailName(name: String) {
        logger.debug("SemTrail description '$name'")

        model.name = name
    }

    override fun onConfigNodeTypes(set: MutableSet<String>) {
        model.nodeTypeSet=set
    }

    override fun onConfigEdgeTypes(set: MutableSet<String>) {
        model.edgeTypeSet=set
    }

    override fun onConfigNodeTypeName(nodeType: String, nodeTypeName: String) {
        model.nodeTypeNames[nodeType]=nodeTypeName
    }

    override fun onConfigNodeTypeColor(nodeType: String, nodeTypeColor: String) {
        model.nodeTypeColors[nodeType]=nodeTypeColor
    }

    override fun onConfigEdgeTypeName(edgeType: String, edgeTypeName: String) {
        model.edgeTypeNames[edgeType]=edgeTypeName
    }

    override fun onConfigEdgeTypeColor(nodeType: String, edgeTypeColor: String) {
        model.edgeTypeColors[nodeType]=edgeTypeColor
    }

    override fun onNode(name: String) {
        logger.debug("Node '$name'")

        if (model.hasNode(name)) {
            logger.error("Node '$name' already exists")
            return
        }

        model.getOrCreateNode(name)
    }

    override fun onNodeAttribute(nodeName: String, attributeName: String, attributeValue: String) {
        val node = model.getNode(nodeName)

        node?.apply {
            when (attributeName) {
                ":type" -> type = attributeValue
                ":doc" -> doc = attributeValue
            }
        }
    }

    override fun onEdge(from: String, type: String, to: String) {
        logger.debug("Edge '$from' $type '$to'")

        val fromNode=model.getOrCreateNode(from)
        val toNode= model.getOrCreateNode(to)

        if (fromNode.hasTargetNode(toNode)) {
            logger.error("From node '${fromNode.name}' already has an edge to node '${toNode.name}'")
            return
        }

        fromNode.addOutgoingEdge(Edge(fromNode,type,toNode))
        toNode.addIncomingEdge(Edge(fromNode,type,toNode))

    }

    override fun onEdgeAttribute(from: String, to: String, attributeName: String, attributeValue: String) {
        val fromNode=model.getNode(from)

        val edge = fromNode?.getEdge(to)

        edge?.apply {
            when (attributeName) {
                ":doc" -> doc = attributeValue
            }
        }
    }

    override fun isValidNodeType(nodeType: String): Boolean {
        return model.nodeTypeSet.contains(nodeType)
    }

    override fun isValidEdgeType(edgeType: String): Boolean {
        return model.edgeTypeSet.contains(edgeType)
    }

}
