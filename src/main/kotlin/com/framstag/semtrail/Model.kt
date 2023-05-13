package com.framstag.semtrail

class Model {
    var name : String = ""

    var nodeTypeSet: MutableSet<String> = mutableSetOf()
    var nodeTypeColors: MutableMap<String,String> = mutableMapOf()

    var nodeTypeNames: MutableMap<String,String> = mutableMapOf()
    var edgeTypeColors: MutableMap<String,String> = mutableMapOf()

    var edgeTypeSet: MutableSet<String> = mutableSetOf()
    var edgeTypeNames: MutableMap<String,String> = mutableMapOf()

    val nodeMap : MutableMap<String,Node> = mutableMapOf()

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
