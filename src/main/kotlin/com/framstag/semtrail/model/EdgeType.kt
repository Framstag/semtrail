package com.framstag.semtrail.model

class EdgeType(val type: String, val name: String, val color: String) : Comparable<EdgeType> {
    override fun compareTo(other: EdgeType): Int {
        return name.compareTo(other.name)
    }
}