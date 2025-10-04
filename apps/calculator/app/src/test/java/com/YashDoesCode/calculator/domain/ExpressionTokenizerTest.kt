package com.YashDoesCode.calculator.domain

import com.YashDoesCode.calculator.domain.evaluator.ExpressionTokenizer
import com.YashDoesCode.calculator.domain.model.Token
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ExpressionTokenizerTest {

    private lateinit var tokenizer: ExpressionTokenizer

    @Before
    fun setUp() {
        tokenizer = ExpressionTokenizer()
    }

    @Test
    fun `tokenize simple arithmetic expression`() {
        val tokens = tokenizer.tokenize("12 + 34 * 56 / 78")

        assertThat(tokens).containsExactly(
            Token.Number(12.0),
            Token.Operator.ADD,
            Token.Number(34.0),
            Token.Operator.MULTIPLY,
            Token.Number(56.0),
            Token.Operator.DIVIDE,
            Token.Number(78.0)
        ).inOrder()
    }

    @Test
    fun `tokenize unary minus at start and after operator`() {
        val tokens = tokenizer.tokenize("-5 * -3")

        assertThat(tokens).containsExactly(
            Token.Operator.UNARY_MINUS,
            Token.Number(5.0),
            Token.Operator.MULTIPLY,
            Token.Operator.UNARY_MINUS,
            Token.Number(3.0)
        ).inOrder()
    }

    @Test
    fun `tokenize implicit multiplication between number and parenthesis`() {
        val tokens = tokenizer.tokenize("2(3 + 4)")

        assertThat(tokens).containsExactly(
            Token.Number(2.0),
            Token.Operator.MULTIPLY,
            Token.Parenthesis.Open,
            Token.Number(3.0),
            Token.Operator.ADD,
            Token.Number(4.0),
            Token.Parenthesis.Close
        ).inOrder()
    }

    @Test
    fun `tokenize implicit multiplication with scientific constants`() {
        val tokens = tokenizer.tokenize("2π + 3e")

        assertThat(tokens).containsExactly(
            Token.Number(2.0),
            Token.Operator.MULTIPLY,
            Token.Constant.PI,
            Token.Operator.ADD,
            Token.Number(3.0),
            Token.Operator.MULTIPLY,
            Token.Constant.E
        ).inOrder()
    }

    @Test
    fun `tokenize functions and factorial`() {
        val tokens = tokenizer.tokenize("sin(0) + 5! + sqrt(16)")

        assertThat(tokens).containsExactly(
            Token.Function.SIN,
            Token.Parenthesis.Open,
            Token.Number(0.0),
            Token.Parenthesis.Close,
            Token.Operator.ADD,
            Token.Number(5.0),
            Token.Operator.FACTORIAL,
            Token.Operator.ADD,
            Token.Function.SQRT,
            Token.Parenthesis.Open,
            Token.Number(16.0),
            Token.Parenthesis.Close
        ).inOrder()
    }
}
