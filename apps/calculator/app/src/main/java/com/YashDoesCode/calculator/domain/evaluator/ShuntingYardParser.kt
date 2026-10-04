package com.YashDoesCode.calculator.domain.evaluator

import com.YashDoesCode.calculator.domain.model.Token

class ShuntingYardParser {

    /**
     * Converts an infix token stream into postfix notation (Reverse Polish Notation)
     * using Dijkstra's Shunting-Yard algorithm.
     */
    fun parseToPostfix(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val operatorStack = ArrayDeque<Token>()

        for (token in tokens) {
            when (token) {
                is Token.Number, is Token.Constant -> {
                    output.add(token)
                }

                is Token.Function -> {
                    operatorStack.addLast(token)
                }

                is Token.Operator -> {
                    while (operatorStack.isNotEmpty()) {
                        val top = operatorStack.last()
                        val shouldPop = when (top) {
                            is Token.Function -> true
                            is Token.Operator -> {
                                if (token.isRightAssociative) {
                                    top.precedence > token.precedence
                                } else {
                                    top.precedence >= token.precedence
                                }
                            }
                            else -> false
                        }

                        if (shouldPop && top != Token.Parenthesis.Open) {
                            output.add(operatorStack.removeLast())
                        } else {
                            break
                        }
                    }
                    operatorStack.addLast(token)
                }

                Token.Parenthesis.Open -> {
                    operatorStack.addLast(token)
                }

                Token.Parenthesis.Close -> {
                    var foundOpen = false
                    while (operatorStack.isNotEmpty()) {
                        val top = operatorStack.removeLast()
                        if (top == Token.Parenthesis.Open) {
                            foundOpen = true
                            break
                        }
                        output.add(top)
                    }
                    if (!foundOpen) {
                        throw IllegalArgumentException("Mismatched parentheses: missing '('")
                    }
                    if (operatorStack.isNotEmpty() && operatorStack.last() is Token.Function) {
                        output.add(operatorStack.removeLast())
                    }
                }
            }
        }

        while (operatorStack.isNotEmpty()) {
            val top = operatorStack.removeLast()
            if (top is Token.Parenthesis) {
                throw IllegalArgumentException("Mismatched parentheses: unmatched parenthesis")
            }
            output.add(top)
        }

        return output
    }
}
