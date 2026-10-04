package com.YashDoesCode.comms.domain.repository

import com.YashDoesCode.comms.domain.model.CallLogEntry
import com.YashDoesCode.comms.domain.model.Contact
import com.YashDoesCode.comms.domain.model.EmailMessage
import com.YashDoesCode.comms.domain.model.EmailThread
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getContacts(): Flow<List<Contact>>
    suspend fun searchContacts(query: String): List<Contact>
    suspend fun syncContacts(): Result<Unit>
}

interface CallLogRepository {
    fun getRecentCalls(): Flow<List<CallLogEntry>>
    suspend fun fetchRecentCalls(): Result<List<CallLogEntry>>
}

interface EmailRepository {
    fun getThreads(): Flow<List<EmailThread>>
    fun getThreadById(threadId: String): Flow<EmailThread?>
    suspend fun sendEmail(recipientEmail: String, subject: String, body: String): Result<EmailMessage>
    suspend fun markThreadAsRead(threadId: String, isRead: Boolean)
    suspend fun toggleStarThread(threadId: String)
    suspend fun deleteThread(threadId: String)
}
