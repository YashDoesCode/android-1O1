package com.YashDoesCode.calculator.domain

import com.YashDoesCode.calculator.domain.evaluator.MathExpressionEvaluator
import com.YashDoesCode.calculator.domain.model.CalculationResult
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class MathExpressionEvaluatorTest {

    private lateinit var evaluator: MathExpressionEvaluator

    @Before
    fun setUp() {
        evaluator = MathExpressionEvaluator()
    }

    @Test
    fun `evaluate basic arithmetic operations`() {
        val result = evaluator.evaluate("10 + 20 * 3 - 50 / 2")
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        val success = result as CalculationResult.Success
        assertThat(success.value).isEqualTo(45.0)
        assertThat(success.formatted).isEqualTo("45")
    }

    @Test
    fun `evaluate parentheses precedence`() {
        val result = evaluator.evaluate("(10 + 20) * (3 - 1)")
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        val success = result as CalculationResult.Success
        assertThat(success.value).isEqualTo(60.0)
        assertThat(success.formatted).isEqualTo("60")
    }

    @Test
    fun `evaluate power and right associativity`() {
        val result = evaluator.evaluate("2 ^ 3 ^ 2")
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        val success = result as CalculationResult.Success
        assertThat(success.value).isEqualTo(512.0)
    }

    @Test
    fun `evaluate unary negation`() {
        val result = evaluator.evaluate("-5 + 8 * -2")
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        val success = result as CalculationResult.Success
        assertThat(success.value).isEqualTo(-21.0)
    }

    @Test
    fun `evaluate division by zero returns DivisionByZero error`() {
        val result = evaluator.evaluate("42 / 0")
        assertThat(result).isEqualTo(CalculationResult.Error.DivisionByZero)
    }

    @Test
    fun `evaluate modulo by zero returns DivisionByZero error`() {
        val result = evaluator.evaluate("42 % 0")
        assertThat(result).isEqualTo(CalculationResult.Error.DivisionByZero)
    }

    @Test
    fun `evaluate trigonometric functions`() {
        val sinZero = evaluator.evaluate("sin(0)")
        assertThat((sinZero as CalculationResult.Success).value).isEqualTo(0.0)

        val cosZero = evaluator.evaluate("cos(0)")
        assertThat((cosZero as CalculationResult.Success).value).isEqualTo(1.0)
    }

    @Test
    fun `evaluate square root of negative number returns DomainError`() {
        val result = evaluator.evaluate("sqrt(-16)")
        assertThat(result).isEqualTo(CalculationResult.Error.DomainError)
    }

    @Test
    fun `evaluate natural logarithm of zero or negative returns DomainError`() {
        val zeroResult = evaluator.evaluate("ln(0)")
        assertThat(zeroResult).isEqualTo(CalculationResult.Error.DomainError)

        val negResult = evaluator.evaluate("log(-10)")
        assertThat(negResult).isEqualTo(CalculationResult.Error.DomainError)
    }

    @Test
    fun `evaluate factorial`() {
        val result = evaluator.evaluate("5!")
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        assertThat((result as CalculationResult.Success).value).isEqualTo(120.0)

        val zeroFactorial = evaluator.evaluate("0!")
        assertThat((zeroFactorial as CalculationResult.Success).value).isEqualTo(1.0)
    }

    @Test
    fun `evaluate implicit multiplication with constants`() {
        val piResult = evaluator.evaluate("2π")
        assertThat(piResult).isInstanceOf(CalculationResult.Success::class.java)
        assertThat((piResult as CalculationResult.Success).value).isWithin(1e-5).of(2 * Math.PI)
    }

    @Test
    fun `evaluate decimal formatting cleanly removes trailing zeros`() {
        assertThat(evaluator.formatNumber(14.0)).isEqualTo("14")
        assertThat(evaluator.formatNumber(14.5)).isEqualTo("14.5")
        assertThat(evaluator.formatNumber(0.0)).isEqualTo("0")
    }

    @Test
    fun `evaluate empty string returns zero`() {
        val result = evaluator.evaluate("")
        assertThat(result).isInstanceOf(CalculationResult.Success::class.java)
        assertThat((result as CalculationResult.Success).formatted).isEqualTo("0")
    }
}
