package com.YashDoesCode.calculator.ui

import com.YashDoesCode.calculator.domain.model.CalculatorAction
import com.YashDoesCode.calculator.domain.model.Token
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel()
    }

    @Test
    fun `typing digits and operator updates expression and preview`() {
        viewModel.onAction(CalculatorAction.Number(5))
        viewModel.onAction(CalculatorAction.Op(Token.Operator.ADD))
        viewModel.onAction(CalculatorAction.Number(7))

        val state = viewModel.uiState.value
        assertThat(state.expression).isEqualTo("5 + 7")
        assertThat(state.previewResult).isEqualTo("12")
    }

    @Test
    fun `calculate action finalizes result and adds to history`() {
        viewModel.onAction(CalculatorAction.Number(8))
        viewModel.onAction(CalculatorAction.Op(Token.Operator.MULTIPLY))
        viewModel.onAction(CalculatorAction.Number(6))
        viewModel.onAction(CalculatorAction.Calculate)

        val state = viewModel.uiState.value
        assertThat(state.expression).isEqualTo("48")
        assertThat(state.previewResult).isEqualTo("48")
        assertThat(state.history).hasSize(1)
        assertThat(state.history[0].expression).isEqualTo("8 × 6")
        assertThat(state.history[0].result).isEqualTo("48")
    }

    @Test
    fun `division by zero sets error message`() {
        viewModel.onAction(CalculatorAction.Number(9))
        viewModel.onAction(CalculatorAction.Op(Token.Operator.DIVIDE))
        viewModel.onAction(CalculatorAction.Number(0))
        viewModel.onAction(CalculatorAction.Calculate)

        val state = viewModel.uiState.value
        assertThat(state.errorMessage).isEqualTo("Cannot divide by zero")
    }

    @Test
    fun `delete action removes last character or operator`() {
        viewModel.onAction(CalculatorAction.Number(1))
        viewModel.onAction(CalculatorAction.Number(2))
        viewModel.onAction(CalculatorAction.Delete)

        val state = viewModel.uiState.value
        assertThat(state.expression).isEqualTo("1")
    }

    @Test
    fun `toggle scientific updates expansion state`() {
        assertThat(viewModel.uiState.value.isScientificExpanded).isFalse()
        viewModel.onAction(CalculatorAction.ToggleScientific)
        assertThat(viewModel.uiState.value.isScientificExpanded).isTrue()
        viewModel.onAction(CalculatorAction.ToggleScientific)
        assertThat(viewModel.uiState.value.isScientificExpanded).isFalse()
    }

    @Test
    fun `clear action resets state completely`() {
        viewModel.onAction(CalculatorAction.Number(9))
        viewModel.onAction(CalculatorAction.Clear)

        val state = viewModel.uiState.value
        assertThat(state.expression).isEmpty()
        assertThat(state.previewResult).isEqualTo("0")
        assertThat(state.errorMessage).isNull()
    }
}
