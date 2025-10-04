package com.YashDoesCode.calculator.domain.evaluator

import com.YashDoesCode.calculator.domain.model.Token

class ExpressionTokenizer {

    fun tokenize(expression: String): List<Token> {
        val sanitized = expression
            .replace(" ", "")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("√", "sqrt")
            .replace("π", "pi")

        val tokens = mutableListOf<Token>()
        var index = 0
        val length = sanitized.length

        while (index < length) {
            val char = sanitized[index]

            when {
                char.isDigit() || char == '.' -> {
                    val start = index
                    while (index < length && (sanitized[index].isDigit() || sanitized[index] == '.')) {
                        index++
                    }
                    val numberStr = sanitized.substring(start, index)
                    val value = numberStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numberStr")
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Number(value))
                }

                char == '+' -> {
                    tokens.add(Token.Operator.ADD)
                    index++
                }

                char == '-' -> {
                    // Check if unary minus: if at start, or preceded by an operator or open paren
                    val isUnary = tokens.isEmpty() ||
                            tokens.last() is Token.Operator ||
                            tokens.last() == Token.Parenthesis.Open

                    if (isUnary) {
                        tokens.add(Token.Operator.UNARY_MINUS)
                    } else {
                        tokens.add(Token.Operator.SUBTRACT)
                    }
                    index++
                }

                char == '*' -> {
                    tokens.add(Token.Operator.MULTIPLY)
                    index++
                }

                char == '/' -> {
                    tokens.add(Token.Operator.DIVIDE)
                    index++
                }

                char == '%' -> {
                    tokens.add(Token.Operator.MODULO)
                    index++
                }

                char == '^' -> {
                    tokens.add(Token.Operator.POWER)
                    index++
                }

                char == '!' -> {
                    tokens.add(Token.Operator.FACTORIAL)
                    index++
                }

                char == '(' -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Parenthesis.Open)
                    index++
                }

                char == ')' -> {
                    tokens.add(Token.Parenthesis.Close)
                    index++
                }

                sanitized.startsWith("sin", index) -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Function.SIN)
                    index += 3
                }

                sanitized.startsWith("cos", index) -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Function.COS)
                    index += 3
                }

                sanitized.startsWith("tan", index) -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Function.TAN)
                    index += 3
                }

                sanitized.startsWith("ln", index) -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Function.LN)
                    index += 2
                }

                sanitized.startsWith("log", index) -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Function.LOG10)
                    index += 3
                }

                sanitized.startsWith("sqrt", index) -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Function.SQRT)
                    index += 4
                }

                sanitized.startsWith("pi", index) -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Constant.PI)
                    index += 2
                }

                char == 'e' -> {
                    insertImplicitMultiplicationIfNeeded(tokens)
                    tokens.add(Token.Constant.E)
                    index++
                }

                else -> {
                    throw IllegalArgumentException("Unexpected character '$char' at position $index")
                }
            }
        }

        return tokens
    }

    private fun insertImplicitMultiplicationIfNeeded(tokens: MutableList<Token>) {
        if (tokens.isEmpty()) return
        val last = tokens.last()
        if (last is Token.Number ||
            last is Token.Constant ||
            last == Token.Parenthesis.Close ||
            last == Token.Operator.FACTORIAL
        ) {
            tokens.add(Token.Operator.MULTIPLY)
        }
    }
}
