package com.framstag.semtrail

import mu.KLogging

class Parser(private val scanner: Scanner,
             private val callback: ASTCallback) {
    companion object : KLogging()

    private fun expectedTokenType(token : Token, expectedType : TokenType) {
        logger.error("${token.row}, ${token.column}: Expected $expectedType, but got ${token.getRef()}")
    }

    private fun expectedTokenType(token : Token, description: String, expectedType : TokenType) {
        logger.error("${token.row}, ${token.column}: Expected $description ($expectedType), but got ${token.getRef()}")
    }

    private fun expectedSymbolValue(token: Token, value: String) {
        logger.error("${token.row}, ${token.column}: Expected symbol value '$value', but got ${token.getRef()}")
    }

    private fun expectedEOL(token: Token) {
        logger.error("${token.row}, ${token.column}: Expected EOL")
    }

    private fun syntacticError(token: Token, description: String) {
        logger.error("${token.row}, ${token.column}: '$description")
    }

    private fun semanticError(token: Token, description: String) {
        logger.error("${token.row}, ${token.column}: '$description")
    }

    fun parse(): Boolean {
        scanner.nextToken()

        if (scanner.token.type!=TokenType.LEFT_BRACKET) {
            expectedTokenType(scanner.token,"(",TokenType.LEFT_BRACKET)
            return false
        }

        scanner.nextToken()

        if (scanner.token.type!=TokenType.SYMBOL || scanner.token.value!="semtrail") {
            expectedSymbolValue(scanner.token,"semtrail")
            return false
        }

        scanner.nextToken()

        if (scanner.token.type!=TokenType.STRING) {
            expectedTokenType(scanner.token,"project name",TokenType.STRING)
            return false
        }

        callback.onSemtrailName(scanner.token.value)

        if (!parseBody()) {
            return false
        }

        if (scanner.token.type!=TokenType.RIGHT_BRACKET) {
            expectedTokenType(scanner.token,")",TokenType.RIGHT_BRACKET)
            return false
        }

        scanner.nextToken()

        if (scanner.token.type!=TokenType.EOL) {
            expectedEOL(scanner.token)
            return false
        }

        return true
    }

    private fun parseBody():Boolean {
        scanner.nextToken()

        while (scanner.token.type==TokenType.LEFT_BRACKET) {
            scanner.nextToken()

            if (scanner.token.type!=TokenType.SYMBOL) {
                expectedTokenType(scanner.token,")",TokenType.RIGHT_BRACKET)
                return false
            }

            val knownFunction = when (scanner.token.value) {
                "config" -> parseConfig()
                "node" -> parseNode()
                "edge" -> parseEdge()
                else -> false
            }

            if (!knownFunction) {
                semanticError(scanner.token,"'${scanner.token.value}' is not a known function")
                return false
            }

            if (scanner.token.type!=TokenType.RIGHT_BRACKET) {
                expectedTokenType(scanner.token,")",TokenType.RIGHT_BRACKET)

                return false
            }

            scanner.nextToken()
        }

        return true
    }

    private fun parseConfig(): Boolean {
        scanner.nextToken()

        if (scanner.token.type==TokenType.LEFT_CURLY_BRACKET) {
            scanner.nextToken()

            while (scanner.token.type!=TokenType.EOL &&
                scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {

                if (scanner.token.type!=TokenType.ATOM) {
                    expectedTokenType(scanner.token, "config attribute", TokenType.ATOM)
                    return false
                }

                val knownAttribute = when (scanner.token.value) {
                    ":nodeTypes" -> parseConfigNodeTypes()
                    ":edgeTypes" -> parseConfigEdgeTypes()
                    ":nodeTypeNames" -> parseConfigNodeTypeNames()
                    ":edgeTypeNames" -> parseConfigEdgeTypeNames()
                    else -> {
                        semanticError(scanner.token,"'${scanner.token.value}' is not a known config attribute")
                        return false
                    }
                }

                if (!knownAttribute) {
                    return false
                }
            }

            if (scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {
                expectedTokenType(scanner.token,"}", TokenType.RIGHT_CURLY_BRACKET)
                return false
            }

            scanner.nextToken()
        }

        return true
    }

    private fun parseConfigNodeTypes():Boolean {
        val set: MutableSet<String> = mutableSetOf()

        scanner.nextToken()
        parseAtomSet(set)

        callback.onConfigNodeTypes(set)

        return true
    }

    private fun parseConfigEdgeTypes():Boolean {
        val set: MutableSet<String> = mutableSetOf()

        scanner.nextToken()
        parseAtomSet(set)

        callback.onConfigEdgeTypes(set)

        return true
    }

    private fun parseConfigNodeTypeNames():Boolean {
        scanner.nextToken()

        if (scanner.token.type!=TokenType.LEFT_CURLY_BRACKET) {
            expectedTokenType(scanner.token,"{", TokenType.LEFT_CURLY_BRACKET)
            return false
        }

        scanner.nextToken()

        while (scanner.token.type!=TokenType.EOL &&
            scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {

            if (scanner.token.type!=TokenType.ATOM) {
                expectedTokenType(scanner.token, "node type name", TokenType.ATOM)
                return false
            }

            val nodeType = scanner.token.value

            if (!callback.isValidNodeType(nodeType)) {
                semanticError(scanner.token,"$nodeType is not a valid node type")
            }

            scanner.nextToken()

            if (scanner.token.type!=TokenType.STRING) {
                expectedTokenType(scanner.token,"node type name",TokenType.STRING)
                return false
            }

            val nodeTypeName = scanner.token.value

            callback.onConfigNodeTypeName(nodeType,nodeTypeName)

            scanner.nextToken()
        }

        if (scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {
            expectedTokenType(scanner.token,"}",TokenType.RIGHT_CURLY_BRACKET)
            return false
        }

        scanner.nextToken()

        return true
    }

    private fun parseConfigEdgeTypeNames():Boolean {
        scanner.nextToken()

        if (scanner.token.type!=TokenType.LEFT_CURLY_BRACKET) {
            expectedTokenType(scanner.token,"{", TokenType.LEFT_CURLY_BRACKET)
            return false
        }

        scanner.nextToken()

        while (scanner.token.type!=TokenType.EOL &&
            scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {

            if (scanner.token.type!=TokenType.ATOM) {
                expectedTokenType(scanner.token, "edge type name", TokenType.ATOM)
                return false
            }

            val edgeType = scanner.token.value

            if (!callback.isValidEdgeType(edgeType)) {
                semanticError(scanner.token,"$edgeType is not a valid edge type")
            }

            scanner.nextToken()

            if (scanner.token.type!=TokenType.STRING) {
                expectedTokenType(scanner.token,"edge type name",TokenType.STRING)
                return false
            }

            val edgeTypeName = scanner.token.value

            callback.onConfigEdgeTypeName(edgeType,edgeTypeName)

            scanner.nextToken()
        }

        if (scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {
            expectedTokenType(scanner.token,"}",TokenType.RIGHT_CURLY_BRACKET)
            return false
        }

        scanner.nextToken()

        return true
    }

    private fun parseNode(): Boolean {
        scanner.nextToken()

        if (scanner.token.type!=TokenType.STRING) {
            expectedTokenType(scanner.token,"node name",TokenType.STRING)
            return false
        }

        val nodeName=scanner.token.value

        callback.onNode(nodeName)

        scanner.nextToken()

        if (scanner.token.type==TokenType.LEFT_CURLY_BRACKET) {
            scanner.nextToken()

            while (scanner.token.type!=TokenType.EOL &&
                scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {

                if (scanner.token.type!=TokenType.ATOM) {
                    expectedTokenType(scanner.token, "attribute name", TokenType.ATOM)
                    return false
                }

                val knownAttribute = when (scanner.token.value) {
                    ":type" -> true
                    ":doc" -> true
                    else -> false
                }

                val attributeName = scanner.token

                if (!knownAttribute) {
                    semanticError(scanner.token,"not a known node attribute")
                    return false
                }

                scanner.nextToken()

                if (scanner.token.type!=TokenType.STRING && scanner.token.type!=TokenType.ATOM) {
                    syntacticError(scanner.token,"Expected node attribute value")
                    return false
                }

                val attributeValue = scanner.token

                callback.onNodeAttribute(nodeName,attributeName.value,attributeValue.value)

                scanner.nextToken()
            }

            if (scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {
                expectedTokenType(scanner.token,"}",TokenType.RIGHT_CURLY_BRACKET)
                return false
            }

            scanner.nextToken()
        }

        return true
    }

    private fun parseEdge(): Boolean {
        scanner.nextToken()

        if (scanner.token.type!=TokenType.STRING) {
            expectedTokenType(scanner.token,"edge start node name", TokenType.STRING)
            return false
        }

        val from = scanner.token.value

        scanner.nextToken()

        if (scanner.token.type!=TokenType.ATOM) {
            expectedTokenType(scanner.token,"edge type", TokenType.ATOM)
            return false
        }

        val type = scanner.token.value

        if (!callback.isValidEdgeType(type)) {
            semanticError(scanner.token,"'$type' is not a valid edge type")
            return false
        }

        scanner.nextToken()

        if (scanner.token.type!=TokenType.STRING) {
            expectedTokenType(scanner.token,"edge end node name", TokenType.STRING)
            return false
        }

        val to = scanner.token.value

        callback.onEdge(from,type,to)

        scanner.nextToken()

        if (scanner.token.type==TokenType.LEFT_CURLY_BRACKET) {
            scanner.nextToken()

            while (scanner.token.type!=TokenType.EOL &&
                scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {
                scanner.nextToken()
            }

            if (scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {
                expectedTokenType(scanner.token,TokenType.RIGHT_CURLY_BRACKET)
                return false
            }

            scanner.nextToken()
        }

        return true
    }

    private fun parseAtomSet(set: MutableSet<String>): Boolean {
        if (scanner.token.type!=TokenType.LEFT_BRACKET) {
            expectedTokenType(scanner.token,"(",TokenType.LEFT_BRACKET)
            return false
        }

        scanner.nextToken()

        while (scanner.token.type==TokenType.ATOM) {
            set.add(scanner.token.value)

            scanner.nextToken()
        }

        if (scanner.token.type!=TokenType.RIGHT_BRACKET) {
            expectedTokenType(scanner.token,")",TokenType.RIGHT_BRACKET)
            return false
        }

        scanner.nextToken()

        return true
    }
}