package com.YashDoesCode.comms.domain.usecase

import com.YashDoesCode.comms.domain.model.CallLogEntry
import com.YashDoesCode.comms.domain.model.Contact
import com.YashDoesCode.comms.domain.repository.CallLogRepository
import com.YashDoesCode.comms.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow

class GetContactsUseCase(private val repository: ContactRepository) {
    operator fun invoke(): Flow<List<Contact>> = repository.getContacts()
}

class SearchContactsUseCase(private val repository: ContactRepository) {
    suspend operator fun invoke(query: String): List<Contact> = repository.searchContacts(query)
}

class SyncContactsUseCase(private val repository: ContactRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.syncContacts()
}

class GetRecentCallsUseCase(private val repository: CallLogRepository) {
    operator fun invoke(): Flow<List<CallLogEntry>> = repository.getRecentCalls()
}

class FetchRecentCallsUseCase(private val repository: CallLogRepository) {
    suspend operator fun invoke(): Result<List<CallLogEntry>> = repository.fetchRecentCalls()
}
