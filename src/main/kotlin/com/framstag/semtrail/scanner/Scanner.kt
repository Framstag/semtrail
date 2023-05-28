package com.framstag.semtrail

import java.io.File


class Scanner(filename: String) {
    private var buffer = ""
    private var nextPos = 0
    private var column = 1
    private var row = 1

    var token = Token(TokenType.EOL,"",column,row)

    init {
        buffer = File(filename).readText()
    }

    private fun eol():Boolean {
        return nextPos >= buffer.length
    }

    private fun nextChar():Char {
        return buffer[nextPos]
    }

    private fun advance() {
        val oldPos = nextPos

        nextPos++

        if (buffer[oldPos] == '\n') {
            row++
            column = 1
        } else {
            column++
        }
    }

    private fun consumeWhiteSpaceAndComments() {
        while (!eol() &&
                (nextChar().isWhitespace() || nextChar()==';')) {
            if (nextChar().isWhitespace()) {
                advance()
            }
            else {
                while (!eol() && nextChar()!='\n') {
                    advance()
                }
            }
        }
    }

    private fun consumeLeftBracket():Boolean {
        token = Token(TokenType.LEFT_BRACKET,"(",column,row)
        advance()

        return true
    }

    private fun consumeRightBracket():Boolean {
        token = Token(TokenType.RIGHT_BRACKET,")",column,row)
        advance()

        return true
    }

    private fun consumeLeftCurlyBracket():Boolean {
        token = Token(TokenType.LEFT_CURLY_BRACKET,"{",column,row)
        advance()

        return true
    }

    private fun consumeRightCurlyBracket():Boolean {
        token = Token(TokenType.RIGHT_CURLY_BRACKET,"}",column,row)
        advance()

        return true
    }

    private fun consumeQuote():Boolean {
        token = Token(TokenType.QUOTE,"'",column,row)
        advance()

        return true
    }

    private fun consumeAtom():Boolean {
        val startPos=nextPos

        advance()

        while (!nextChar().isWhitespace() &&
                nextChar()!='(' &&
                nextChar()!=')' &&
                nextChar()!='{' &&
                nextChar()!='}' &&
                nextChar()!=';') {
            advance()
        }

        token = Token(TokenType.ATOM,buffer.substring(startPos,nextPos),column,row)

        return true
    }

    private fun consumeString():Boolean {
        advance()

        val startPos=nextPos

        while (!eol() && nextChar()!='"') {
            advance()
        }

        if (eol()) {
            // TODO: Error
            return false
        }

        advance()

        token = Token(TokenType.STRING,buffer.substring(startPos,nextPos-1),column,row)

        return true
    }

    private fun consumeSymbol():Boolean {
        val startPos=nextPos

        while (!eol() &&
                !nextChar().isWhitespace() &&
                nextChar()!='(' &&
                nextChar()!=')' &&
                nextChar()!='{' &&
                nextChar()!='}' &&
                nextChar()!=';') {
            advance()
        }

        token = Token(TokenType.SYMBOL,buffer.substring(startPos,nextPos),column,row)

        return true
    }

    fun nextToken():Boolean {
        consumeWhiteSpaceAndComments()

        if (eol()) {
            token = Token(TokenType.EOL,"",column,row)
            return false
        }

        when {
            nextChar()=='(' -> {
                return consumeLeftBracket()
            }
            nextChar()==')' -> {
                return consumeRightBracket()
            }
            nextChar()=='{' -> {
                return consumeLeftCurlyBracket()
            }
            nextChar()=='}' -> {
                return consumeRightCurlyBracket()
            }
            nextChar()=='\'' -> {
                return consumeQuote()
            }

            // Atom
            nextChar()==':' -> {
                return consumeAtom()
            }

            // String
            nextChar()=='"' -> {
                return consumeString()
            }

            // Symbol
            else -> {
                return consumeSymbol()
            }
        }
    }
}