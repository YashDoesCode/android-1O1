package com.YashDoesCode.comms.domain.model

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val photoUri: String? = null,
    val lookupKey: String? = null
)

enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED,
    REJECTED,
    UNKNOWN
}

data class CallLogEntry(
    val id: Long,
    val number: String,
    val callerName: String? = null,
    val type: CallType,
    val timestamp: Long,
    val durationSeconds: Long
)

data class EmailMessage(
    val id: String,
    val threadId: String,
    val senderName: String,
    val senderEmail: String,
    val recipientEmail: String,
    val subject: String,
    val body: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isStarred: Boolean = false
)

data class EmailThread(
    val id: String,
    val senderName: String,
    val senderEmail: String,
    val subject: String,
    val snippet: String,
    val lastMessageTimestamp: Long,
    val unreadCount: Int = 0,
    val isStarred: Boolean = false,
    val messages: List<EmailMessage> = emptyList()
)
