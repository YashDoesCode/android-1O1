package com.YashDoesCode.comms.domain.usecase

import com.YashDoesCode.comms.domain.model.EmailMessage
import com.YashDoesCode.comms.domain.model.EmailThread
import com.YashDoesCode.comms.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow

class GetEmailThreadsUseCase(private val repository: EmailRepository) {
    operator fun invoke(): Flow<List<EmailThread>> = repository.getThreads()
}

class GetEmailThreadByIdUseCase(private val repository: EmailRepository) {
    operator fun invoke(threadId: String): Flow<EmailThread?> = repository.getThreadById(threadId)
}

class SendEmailUseCase(private val repository: EmailRepository) {
    suspend operator fun invoke(
        recipientEmail: String,
        subject: String,
        body: String
    ): Result<EmailMessage> = repository.sendEmail(recipientEmail, subject, body)
}

class MarkThreadAsReadUseCase(private val repository: EmailRepository) {
    suspend operator fun invoke(threadId: String, isRead: Boolean) =
        repository.markThreadAsRead(threadId, isRead)
}

class ToggleStarThreadUseCase(private val repository: EmailRepository) {
    suspend operator fun invoke(threadId: String) = repository.toggleStarThread(threadId)
}

class DeleteEmailThreadUseCase(private val repository: EmailRepository) {
    suspend operator fun invoke(threadId: String) = repository.deleteThread(threadId)
}
