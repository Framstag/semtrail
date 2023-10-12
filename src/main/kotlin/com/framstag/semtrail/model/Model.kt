package com.framstag.semtrail.model

class Model {
    var name : String = ""

    val nodeTypes : MutableMap<String, NodeType> = mutableMapOf()
    val edgeTypes : MutableMap<String, EdgeType> = mutableMapOf()
    val nodeMap : MutableMap<String, Node> = mutableMapOf()

    fun sortedNodeTypes (): MutableList<NodeType>? {
        return nodeTypes.values.stream().sorted().toList()
    }

    fun sortedEdgeTypes (): MutableList<EdgeType>? {
        return edgeTypes.values.stream().sorted().toList()
    }

    fun hasNode(name: String):Boolean {
        return nodeMap.contains(name)
    }

    fun getNode(name: String): Node? {
        return nodeMap.get(name)
    }

    fun getOrCreateNode(name: String): Node {
        return nodeMap.computeIfAbsent(name) {
                nodeName -> Node(nodeName)
        }
    }
}
