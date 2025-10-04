package com.YashDoesCode.comms.ui.dialer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.comms.di.CommsContainer
import com.YashDoesCode.comms.domain.usecase.FetchRecentCallsUseCase
import com.YashDoesCode.comms.domain.usecase.GetContactsUseCase
import com.YashDoesCode.comms.domain.usecase.GetRecentCallsUseCase
import com.YashDoesCode.comms.domain.usecase.SyncContactsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DialerViewModel(
    private val getContactsUseCase: GetContactsUseCase,
    private val syncContactsUseCase: SyncContactsUseCase,
    private val getRecentCallsUseCase: GetRecentCallsUseCase,
    private val fetchRecentCallsUseCase: FetchRecentCallsUseCase
) : ViewModel() {

    constructor(container: CommsContainer) : this(
        getContactsUseCase = container.getContactsUseCase,
        syncContactsUseCase = container.syncContactsUseCase,
        getRecentCallsUseCase = container.getRecentCallsUseCase,
        fetchRecentCallsUseCase = container.fetchRecentCallsUseCase
    )

    private val _uiState = MutableStateFlow(DialerUiState())
    val uiState: StateFlow<DialerUiState> = _uiState.asStateFlow()

    init {
        observeContacts()
        observeRecentCalls()
    }

    fun onAction(action: DialerAction) {
        when (action) {
            is DialerAction.AppendDigit -> appendDigit(action.digit)
            is DialerAction.DeleteDigit -> deleteDigit()
            is DialerAction.ClearDigits -> _uiState.update { it.copy(digits = "") }
            is DialerAction.SetDigits -> _uiState.update { it.copy(digits = action.digits) }
            is DialerAction.SelectSubTab -> _uiState.update { it.copy(activeSubTab = action.tab) }
            is DialerAction.UpdateSearchQuery -> _uiState.update { it.copy(searchQuery = action.query) }
            is DialerAction.UpdatePermissions -> updatePermissions(action.hasContacts, action.hasCallLog)
            is DialerAction.Refresh -> refreshData()
        }
    }

    private fun appendDigit(digit: Char) {
        _uiState.update { it.copy(digits = it.digits + digit) }
    }

    private fun deleteDigit() {
        _uiState.update { state ->
            if (state.digits.isNotEmpty()) {
                state.copy(digits = state.digits.dropLast(1))
            } else {
                state
            }
        }
    }

    private fun updatePermissions(hasContacts: Boolean, hasCallLog: Boolean) {
        _uiState.update {
            it.copy(
                hasContactsPermission = hasContacts,
                hasCallLogPermission = hasCallLog
            )
        }
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            syncContactsUseCase()
            fetchRecentCallsUseCase().onSuccess { recent ->
                _uiState.update { it.copy(recentCalls = recent) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun observeContacts() {
        viewModelScope.launch {
            getContactsUseCase().collect { contacts ->
                _uiState.update { it.copy(contacts = contacts) }
            }
        }
    }

    private fun observeRecentCalls() {
        viewModelScope.launch {
            getRecentCallsUseCase().collect { recents ->
                _uiState.update { it.copy(recentCalls = recents) }
            }
        }
    }

    class Factory(private val container: CommsContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DialerViewModel(container) as T
        }
    }
}
