package com.framstag.semtrail

interface ASTCallback {
    fun onSemtrailName(name: String)

    fun onConfigNodeTypes(set: MutableSet<String>)
    fun onConfigEdgeTypes(set: MutableSet<String>)

    fun onConfigNodeTypeName(nodeType: String, nodeTypeName: String)
    fun onConfigEdgeTypeName(edgeType: String, edgeTypeName: String)

    fun onNode(name: String)

    fun onNodeAttribute(nodeName: String, attributeName: String, attributeValue: String)

    fun onEdge(from: String, type: String, to: String)

    fun isValidNodeType(nodeType: String):Boolean
    fun isValidEdgeType(edgeType: String):Boolean
}