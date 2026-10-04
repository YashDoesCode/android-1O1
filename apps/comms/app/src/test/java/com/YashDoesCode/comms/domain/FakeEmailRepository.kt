package com.YashDoesCode.comms.domain

import com.YashDoesCode.comms.domain.model.EmailMessage
import com.YashDoesCode.comms.domain.model.EmailThread
import com.YashDoesCode.comms.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FakeEmailRepository : EmailRepository {

    private val _threads = MutableStateFlow<List<EmailThread>>(emptyList())

    fun setThreads(threads: List<EmailThread>) {
        _threads.value = threads
    }

    override fun getThreads(): Flow<List<EmailThread>> = _threads

    override fun getThreadById(threadId: String): Flow<EmailThread?> {
        return _threads.map { list -> list.find { it.id == threadId } }
    }

    override suspend fun sendEmail(recipientEmail: String, subject: String, body: String): Result<EmailMessage> {
        val now = System.currentTimeMillis()
        val newMessage = EmailMessage(
            id = UUID.randomUUID().toString(),
            threadId = "sent-${UUID.randomUUID()}",
            senderName = "Me",
            senderEmail = "me@example.com",
            recipientEmail = recipientEmail,
            subject = subject,
            body = body,
            timestamp = now,
            isRead = true
        )
        val newThread = EmailThread(
            id = newMessage.threadId,
            senderName = "Me",
            senderEmail = "me@example.com",
            subject = subject,
            snippet = body.take(100),
            lastMessageTimestamp = now,
            unreadCount = 0,
            isStarred = false,
            messages = listOf(newMessage)
        )
        _threads.value = listOf(newThread) + _threads.value
        return Result.success(newMessage)
    }

    override suspend fun markThreadAsRead(threadId: String, isRead: Boolean) {
        _threads.value = _threads.value.map { thread ->
            if (thread.id == threadId) {
                thread.copy(
                    unreadCount = if (isRead) 0 else 1,
                    messages = thread.messages.map { it.copy(isRead = isRead) }
                )
            } else thread
        }
    }

    override suspend fun toggleStarThread(threadId: String) {
        _threads.value = _threads.value.map { thread ->
            if (thread.id == threadId) {
                thread.copy(isStarred = !thread.isStarred)
            } else thread
        }
    }

    override suspend fun deleteThread(threadId: String) {
        _threads.value = _threads.value.filterNot { it.id == threadId }
    }
}
