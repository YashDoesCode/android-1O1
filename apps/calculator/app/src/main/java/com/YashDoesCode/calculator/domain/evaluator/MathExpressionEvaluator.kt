package com.YashDoesCode.calculator.domain.evaluator

import com.YashDoesCode.calculator.domain.model.CalculationResult
import com.YashDoesCode.calculator.domain.model.Token
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

class MathExpressionEvaluator(
    private val tokenizer: ExpressionTokenizer = ExpressionTokenizer(),
    private val parser: ShuntingYardParser = ShuntingYardParser()
) {

    fun evaluate(expression: String): CalculationResult {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            return CalculationResult.Success(0.0, "0")
        }

        return try {
            val tokens = tokenizer.tokenize(trimmed)
            if (tokens.isEmpty()) {
                return CalculationResult.Success(0.0, "0")
            }
            val postfix = parser.parseToPostfix(tokens)
            val result = evaluatePostfix(postfix)

            if (result.isNaN()) {
                CalculationResult.Error.DomainError
            } else if (result.isInfinite()) {
                CalculationResult.Error.DivisionByZero
            } else {
                CalculationResult.Success(result, formatNumber(result))
            }
        } catch (e: ArithmeticException) {
            CalculationResult.Error.DivisionByZero
        } catch (e: IllegalArgumentException) {
            when {
                e.message?.contains("Mismatched parentheses", ignoreCase = true) == true ->
                    CalculationResult.Error.MismatchedParentheses
                e.message?.contains("Domain error", ignoreCase = true) == true ->
                    CalculationResult.Error.DomainError
                else ->
                    CalculationResult.Error.MalformedExpression
            }
        } catch (e: Exception) {
            CalculationResult.Error.Generic(e.message ?: "Evaluation error")
        }
    }

    private fun evaluatePostfix(postfix: List<Token>): Double {
        val stack = ArrayDeque<Double>()

        for (token in postfix) {
            when (token) {
                is Token.Number -> stack.addLast(token.value)
                is Token.Constant -> stack.addLast(token.value)

                is Token.Operator -> {
                    when (token) {
                        Token.Operator.UNARY_MINUS -> {
                            if (stack.isEmpty()) throw IllegalArgumentException("Malformed expression for unary minus")
                            val operand = stack.removeLast()
                            stack.addLast(-operand)
                        }

                        Token.Operator.FACTORIAL -> {
                            if (stack.isEmpty()) throw IllegalArgumentException("Malformed expression for factorial")
                            val operand = stack.removeLast()
                            if (operand < 0 || operand != floor(operand) || operand > 170) {
                                throw IllegalArgumentException("Domain error: Factorial requires non-negative integer <= 170")
                            }
                            stack.addLast(factorial(operand.toInt()))
                        }

                        Token.Operator.ADD -> {
                            if (stack.size < 2) throw IllegalArgumentException("Insufficient operands for +")
                            val b = stack.removeLast()
                            val a = stack.removeLast()
                            stack.addLast(a + b)
                        }

                        Token.Operator.SUBTRACT -> {
                            if (stack.size < 2) throw IllegalArgumentException("Insufficient operands for -")
                            val b = stack.removeLast()
                            val a = stack.removeLast()
                            stack.addLast(a - b)
                        }

                        Token.Operator.MULTIPLY -> {
                            if (stack.size < 2) throw IllegalArgumentException("Insufficient operands for *")
                            val b = stack.removeLast()
                            val a = stack.removeLast()
                            stack.addLast(a * b)
                        }

                        Token.Operator.DIVIDE -> {
                            if (stack.size < 2) throw IllegalArgumentException("Insufficient operands for /")
                            val b = stack.removeLast()
                            val a = stack.removeLast()
                            if (abs(b) < 1e-15) {
                                throw ArithmeticException("Division by zero")
                            }
                            stack.addLast(a / b)
                        }

                        Token.Operator.MODULO -> {
                            if (stack.size < 2) throw IllegalArgumentException("Insufficient operands for %")
                            val b = stack.removeLast()
                            val a = stack.removeLast()
                            if (abs(b) < 1e-15) {
                                throw ArithmeticException("Division by zero in modulo")
                            }
                            stack.addLast(a % b)
                        }

                        Token.Operator.POWER -> {
                            if (stack.size < 2) throw IllegalArgumentException("Insufficient operands for ^")
                            val b = stack.removeLast()
                            val a = stack.removeLast()
                            val result = a.pow(b)
                            if (result.isNaN()) throw IllegalArgumentException("Domain error in power")
                            stack.addLast(result)
                        }
                    }
                }

                is Token.Function -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Insufficient operands for function ${token.symbol}")
                    val arg = stack.removeLast()
                    val result = when (token) {
                        Token.Function.SIN -> sin(arg)
                        Token.Function.COS -> cos(arg)
                        Token.Function.TAN -> {
                            val modPi = (arg % Math.PI + Math.PI) % Math.PI
                            if (abs(modPi - Math.PI / 2) < 1e-12) {
                                throw IllegalArgumentException("Domain error: tan is undefined at odd multiples of pi/2")
                            }
                            tan(arg)
                        }
                        Token.Function.LN -> {
                            if (arg <= 0.0) throw IllegalArgumentException("Domain error: ln requires positive argument")
                            ln(arg)
                        }
                        Token.Function.LOG10 -> {
                            if (arg <= 0.0) throw IllegalArgumentException("Domain error: log requires positive argument")
                            log10(arg)
                        }
                        Token.Function.SQRT -> {
                            if (arg < 0.0) throw IllegalArgumentException("Domain error: sqrt requires non-negative argument")
                            sqrt(arg)
                        }
                    }
                    stack.addLast(result)
                }

                is Token.Parenthesis -> {
                    throw IllegalArgumentException("Unexpected parenthesis in postfix evaluation")
                }
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("Malformed expression: leftover operands in stack (${stack.size})")
        }

        return stack.removeLast()
    }

    private fun factorial(n: Int): Double {
        var res = 1.0
        for (i in 2..n) {
            res *= i
        }
        return res
    }

    fun formatNumber(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"

        // For exact integers within standard display range
        if (value == floor(value) && abs(value) < 1e12) {
            return value.toLong().toString()
        }

        val symbols = DecimalFormatSymbols(Locale.US)
        val format = if (abs(value) >= 1e12 || (abs(value) < 1e-6 && value != 0.0)) {
            DecimalFormat("0.######E0", symbols)
        } else {
            DecimalFormat("#,##0.########", symbols)
        }
        return format.format(value)
    }
}
