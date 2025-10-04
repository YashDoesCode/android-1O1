package com.YashDoesCode.comms.ui.mail

import com.YashDoesCode.comms.domain.model.EmailThread

data class MailUiState(
    val threads: List<EmailThread> = emptyList(),
    val selectedThread: EmailThread? = null,
    val isComposeOpen: Boolean = false,
    val composeRecipient: String = "",
    val composeSubject: String = "",
    val composeBody: String = "",
    val searchQuery: String = "",
    val filterUnreadOnly: Boolean = false,
    val filterStarredOnly: Boolean = false,
    val isSending: Boolean = false,
    val toastMessage: String? = null
) {
    val filteredThreads: List<EmailThread>
        get() {
            var list = threads
            if (filterUnreadOnly) {
                list = list.filter { it.unreadCount > 0 }
            }
            if (filterStarredOnly) {
                list = list.filter { it.isStarred }
            }
            if (searchQuery.isNotBlank()) {
                list = list.filter {
                    it.senderName.contains(searchQuery, ignoreCase = true) ||
                    it.subject.contains(searchQuery, ignoreCase = true) ||
                    it.snippet.contains(searchQuery, ignoreCase = true)
                }
            }
            return list
        }
}

sealed interface MailAction {
    data class SelectThread(val threadId: String?) : MailAction
    data class ToggleStarThread(val threadId: String) : MailAction
    data class MarkThreadAsRead(val threadId: String, val isRead: Boolean) : MailAction
    data class DeleteThread(val threadId: String) : MailAction
    data class SetComposeOpen(val open: Boolean) : MailAction
    data class UpdateComposeRecipient(val recipient: String) : MailAction
    data class UpdateComposeSubject(val subject: String) : MailAction
    data class UpdateComposeBody(val body: String) : MailAction
    data object SendComposeEmail : MailAction
    data class UpdateSearchQuery(val query: String) : MailAction
    data class SetFilterUnread(val unreadOnly: Boolean) : MailAction
    data class SetFilterStarred(val starredOnly: Boolean) : MailAction
    data object ClearToastMessage : MailAction
}
