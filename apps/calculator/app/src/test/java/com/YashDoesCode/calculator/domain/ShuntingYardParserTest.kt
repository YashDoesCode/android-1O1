package com.YashDoesCode.calculator.domain

import com.YashDoesCode.calculator.domain.evaluator.ExpressionTokenizer
import com.YashDoesCode.calculator.domain.evaluator.ShuntingYardParser
import com.YashDoesCode.calculator.domain.model.Token
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ShuntingYardParserTest {

    private lateinit var tokenizer: ExpressionTokenizer
    private lateinit var parser: ShuntingYardParser

    @Before
    fun setUp() {
        tokenizer = ExpressionTokenizer()
        parser = ShuntingYardParser()
    }

    @Test
    fun `parse respects operator precedence multiplication over addition`() {
        val tokens = tokenizer.tokenize("2 + 3 * 4")
        val postfix = parser.parseToPostfix(tokens)

        assertThat(postfix).containsExactly(
            Token.Number(2.0),
            Token.Number(3.0),
            Token.Number(4.0),
            Token.Operator.MULTIPLY,
            Token.Operator.ADD
        ).inOrder()
    }

    @Test
    fun `parse respects parentheses overriding default precedence`() {
        val tokens = tokenizer.tokenize("(2 + 3) * 4")
        val postfix = parser.parseToPostfix(tokens)

        assertThat(postfix).containsExactly(
            Token.Number(2.0),
            Token.Number(3.0),
            Token.Operator.ADD,
            Token.Number(4.0),
            Token.Operator.MULTIPLY
        ).inOrder()
    }

    @Test
    fun `parse right associative power operator`() {
        val tokens = tokenizer.tokenize("2 ^ 3 ^ 2")
        val postfix = parser.parseToPostfix(tokens)

        assertThat(postfix).containsExactly(
            Token.Number(2.0),
            Token.Number(3.0),
            Token.Number(2.0),
            Token.Operator.POWER,
            Token.Operator.POWER
        ).inOrder()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `parse throws on unmatched opening parenthesis`() {
        val tokens = tokenizer.tokenize("(2 + 3 * 4")
        parser.parseToPostfix(tokens)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `parse throws on unmatched closing parenthesis`() {
        val tokens = tokenizer.tokenize("2 + 3) * 4")
        parser.parseToPostfix(tokens)
    }
}
