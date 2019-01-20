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

    fun nextToken():Boolean {
        // skip whitespace and comments
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

        if (eol()) {
            token = Token(TokenType.EOL,"",column,row)
            return false
        }

        // Brackets
        if (nextChar()=='(') {
            token = Token(TokenType.LEFT_BRACKET,"(",column,row)
            advance()

            return true
        }
        else if (nextChar()==')') {
            token = Token(TokenType.RIGHT_BRACKET,")",column,row)
            advance()

            return true
        }
        else if (nextChar()=='{') {
            token = Token(TokenType.LEFT_CURLY_BRACKET,"{",column,row)
            advance()

            return true
        }
        else if (nextChar()=='}') {
            token = Token(TokenType.RIGHT_CURLY_BRACKET,"}",column,row)
            advance()

            return true
        }

        // Atom
        else if (nextChar()==':') {
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

        // String
        else if (nextChar()=='"') {
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

        // Symbol
        else {
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
    }
}