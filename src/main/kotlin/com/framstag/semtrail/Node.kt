package com.framstag.semtrail

class Node(val name: String) {
    val fromNodes : MutableMap<String, Edge> = mutableMapOf()
    val toNodes : MutableMap<String, Edge> = mutableMapOf()

    var type: String = ""
    var doc: String = ""

    fun hasTargetNode(node: Node):Boolean {
        return toNodes.contains(node.name)
    }

    fun addIncomingEdge(edge: Edge) {
        fromNodes.put(edge.from.name,edge)
    }

    fun addOutgoingEdge(edge: Edge) {
        toNodes.put(edge.to.name,edge)
    }

    fun getEdge(toName: String):Edge? {
        return toNodes[toName]
    }
}