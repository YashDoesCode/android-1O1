package com.YashDoesCode.comms.data.repository

import com.YashDoesCode.comms.data.datasource.InMemoryEmailDataSource
import com.YashDoesCode.comms.domain.model.EmailMessage
import com.YashDoesCode.comms.domain.model.EmailThread
import com.YashDoesCode.comms.domain.repository.EmailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class EmailRepositoryImpl(
    private val dataSource: InMemoryEmailDataSource
) : EmailRepository {

    override fun getThreads(): Flow<List<EmailThread>> =
        dataSource.getThreads().flowOn(Dispatchers.IO)

    override fun getThreadById(threadId: String): Flow<EmailThread?> =
        dataSource.getThreadById(threadId).flowOn(Dispatchers.IO)

    override suspend fun sendEmail(
        recipientEmail: String,
        subject: String,
        body: String
    ): Result<EmailMessage> = withContext(Dispatchers.IO) {
        dataSource.sendEmail(recipientEmail, subject, body)
    }

    override suspend fun markThreadAsRead(threadId: String, isRead: Boolean) =
        withContext(Dispatchers.IO) {
            dataSource.markThreadAsRead(threadId, isRead)
        }

    override suspend fun toggleStarThread(threadId: String) =
        withContext(Dispatchers.IO) {
            dataSource.toggleStarThread(threadId)
        }

    override suspend fun deleteThread(threadId: String) =
        withContext(Dispatchers.IO) {
            dataSource.deleteThread(threadId)
        }
}
