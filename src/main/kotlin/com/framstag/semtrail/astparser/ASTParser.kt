package com.framstag.semtrail.astparser

import com.framstag.semtrail.Scanner
import com.framstag.semtrail.Token
import com.framstag.semtrail.TokenType
import mu.KLogging

class ASTParser(private val scanner: Scanner) {
    companion object : KLogging()

    fun parse():FunctionCall? {
        scanner.nextToken()

        return parseFunctionCall()
    }

    private fun expectedTokenType(token : Token, expectedType : TokenType) {
        logger.error("${token.row}, ${token.column}: Expected $expectedType, but got ${token.getRef()}")
    }

    private fun expectedTokenType(token : Token, description: String, expectedType : TokenType) {
        logger.error("${token.row}, ${token.column}: Expected '$description' ($expectedType), but got ${token.getRef()}")
    }

    private fun syntacticError(token: Token, description: String) {
        logger.error("${token.row}, ${token.column}: '$description")
    }

    private fun unexpectedEOL(token : Token) {
        logger.error("${token.row}, ${token.column}: Unexpected EOL")
    }

    private fun parseFunctionCall():FunctionCall? {
        if (scanner.token.type!= TokenType.LEFT_BRACKET) {
            expectedTokenType(scanner.token,"(", TokenType.LEFT_BRACKET)
            return null
        }

        val functionCall = FunctionCall()

        // Skip "("
        scanner.nextToken()

        while (scanner.token.type!=TokenType.RIGHT_BRACKET) {
            val parameter = parseType()

            if (parameter == null) {
                return null
            }

            functionCall.addParameter(parameter)

            scanner.nextToken()
        }

        return functionCall
    }

    private fun parseQuoted():Type? {
        if (scanner.token.type!= TokenType.QUOTE) {
            expectedTokenType(scanner.token, TokenType.QUOTE)
            return null
        }

        scanner.nextToken()

        if (scanner.token.type == TokenType.LEFT_BRACKET) {
            val list = List()

            scanner.nextToken()

            while (scanner.token.type!=TokenType.RIGHT_BRACKET) {
                val member = parseType()

                if (member == null) {
                    return null
                }

                list.addMember(member)

                scanner.nextToken()
            }

            return list
        }
        else if (scanner.token.type == TokenType.SYMBOL) {
            return Text(scanner.token)
        }

        syntacticError(scanner.token,"Quoting is not define here")
        return null
    }

    private fun parseSymbol():Symbol? {
        if (scanner.token.type!= TokenType.SYMBOL) {
            expectedTokenType(scanner.token, TokenType.SYMBOL)
            return null
        }

        return Symbol(scanner.token)
    }

    private fun parseAtom():Atom? {
        if (scanner.token.type!= TokenType.ATOM) {
            expectedTokenType(scanner.token, TokenType.ATOM)
            return null
        }

        return Atom(scanner.token)
    }

    private fun parseString():Text? {
        if (scanner.token.type!= TokenType.STRING) {
            expectedTokenType(scanner.token, TokenType.STRING)
            return null
        }

        return Text(scanner.token)
    }

    private fun parseMap(): Map? {
        if (scanner.token.type!= TokenType.LEFT_CURLY_BRACKET) {
            expectedTokenType(scanner.token, "{", TokenType.LEFT_CURLY_BRACKET)
            return null
        }

        val map = Map()

        // Skip "{"
        scanner.nextToken()

        var key: Type? = null

        while (scanner.token.type!=TokenType.RIGHT_CURLY_BRACKET) {
            val element = parseType()

            if (element == null) {
                return null
            }

            if (key == null) {
                key = element
            }
            else {
                map.addKeyValue(key, element)
                key = null
            }

            scanner.nextToken()
        }

        if (key != null) {
            syntacticError(scanner.token,"Missing value for key in map")
            return null
        }

        return map
    }

    private fun parseType():Type? {
        val type = when (scanner.token.type) {
            TokenType.LEFT_BRACKET -> {
                parseFunctionCall()
            }

            TokenType.LEFT_CURLY_BRACKET -> {
                parseMap()
            }

            TokenType.QUOTE -> {
                parseQuoted()
            }

            TokenType.SYMBOL -> {
                parseSymbol()
            }

            TokenType.ATOM -> {
                parseAtom()
            }

            TokenType.STRING -> {
                parseString()
            }

            TokenType.RIGHT_BRACKET,
            TokenType.RIGHT_CURLY_BRACKET-> {
                syntacticError(scanner.token, "Unexpected token")
                null
            }

            TokenType.EOL -> {
                unexpectedEOL(scanner.token)
                null
            }

            else -> {
                // Should never happen
                null
            }
        }

        return type
    }
}