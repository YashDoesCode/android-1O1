package com.YashDoesCode.comms.ui.mail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.comms.di.CommsContainer
import com.YashDoesCode.comms.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MailViewModel(
    private val getEmailThreadsUseCase: GetEmailThreadsUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val markThreadAsReadUseCase: MarkThreadAsReadUseCase,
    private val toggleStarThreadUseCase: ToggleStarThreadUseCase,
    private val deleteEmailThreadUseCase: DeleteEmailThreadUseCase
) : ViewModel() {

    constructor(container: CommsContainer) : this(
        getEmailThreadsUseCase = container.getEmailThreadsUseCase,
        sendEmailUseCase = container.sendEmailUseCase,
        markThreadAsReadUseCase = container.markThreadAsReadUseCase,
        toggleStarThreadUseCase = container.toggleStarThreadUseCase,
        deleteEmailThreadUseCase = container.deleteEmailThreadUseCase
    )

    private val _uiState = MutableStateFlow(MailUiState())
    val uiState: StateFlow<MailUiState> = _uiState.asStateFlow()

    init {
        observeThreads()
    }

    fun onAction(action: MailAction) {
        when (action) {
            is MailAction.SelectThread -> selectThread(action.threadId)
            is MailAction.ToggleStarThread -> toggleStar(action.threadId)
            is MailAction.MarkThreadAsRead -> markAsRead(action.threadId, action.isRead)
            is MailAction.DeleteThread -> deleteThread(action.threadId)
            is MailAction.SetComposeOpen -> _uiState.update { it.copy(isComposeOpen = action.open) }
            is MailAction.UpdateComposeRecipient -> _uiState.update { it.copy(composeRecipient = action.recipient) }
            is MailAction.UpdateComposeSubject -> _uiState.update { it.copy(composeSubject = action.subject) }
            is MailAction.UpdateComposeBody -> _uiState.update { it.copy(composeBody = action.body) }
            is MailAction.SendComposeEmail -> sendEmail()
            is MailAction.UpdateSearchQuery -> _uiState.update { it.copy(searchQuery = action.query) }
            is MailAction.SetFilterUnread -> _uiState.update { it.copy(filterUnreadOnly = action.unreadOnly) }
            is MailAction.SetFilterStarred -> _uiState.update { it.copy(filterStarredOnly = action.starredOnly) }
            is MailAction.ClearToastMessage -> _uiState.update { it.copy(toastMessage = null) }
        }
    }

    private fun selectThread(threadId: String?) {
        if (threadId == null) {
            _uiState.update { it.copy(selectedThread = null) }
        } else {
            val thread = _uiState.value.threads.find { it.id == threadId }
            _uiState.update { it.copy(selectedThread = thread) }
            if (thread != null && thread.unreadCount > 0) {
                markAsRead(threadId, true)
            }
        }
    }

    private fun toggleStar(threadId: String) {
        viewModelScope.launch {
            toggleStarThreadUseCase(threadId)
        }
    }

    private fun markAsRead(threadId: String, isRead: Boolean) {
        viewModelScope.launch {
            markThreadAsReadUseCase(threadId, isRead)
        }
    }

    private fun deleteThread(threadId: String) {
        viewModelScope.launch {
            deleteEmailThreadUseCase(threadId)
            _uiState.update {
                if (it.selectedThread?.id == threadId) {
                    it.copy(selectedThread = null, toastMessage = "Thread deleted")
                } else {
                    it.copy(toastMessage = "Thread deleted")
                }
            }
        }
    }

    private fun sendEmail() {
        val recipient = _uiState.value.composeRecipient.trim()
        val subject = _uiState.value.composeSubject.trim()
        val body = _uiState.value.composeBody.trim()

        if (recipient.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Please enter a recipient email") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            val result = sendEmailUseCase(recipient, subject.ifBlank { "(No Subject)" }, body)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSending = false,
                        isComposeOpen = false,
                        composeRecipient = "",
                        composeSubject = "",
                        composeBody = "",
                        toastMessage = "Email sent to $recipient"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        toastMessage = "Failed to send: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun observeThreads() {
        viewModelScope.launch {
            getEmailThreadsUseCase().collect { threads ->
                _uiState.update { state ->
                    val updatedSelected = if (state.selectedThread != null) {
                        threads.find { it.id == state.selectedThread.id }
                    } else null
                    state.copy(threads = threads, selectedThread = updatedSelected)
                }
            }
        }
    }

    class Factory(private val container: CommsContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MailViewModel(container) as T
        }
    }
}
