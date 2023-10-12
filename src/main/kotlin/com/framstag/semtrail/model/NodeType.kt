package com.framstag.semtrail.model

class NodeType(val type: String, val name: String, val color: String) : Comparable<NodeType> {
    override fun compareTo(other: NodeType): Int {
        return name.compareTo(other.name)
    }
}