package com.YashDoesCode.comms.domain

import com.YashDoesCode.comms.domain.model.CallLogEntry
import com.YashDoesCode.comms.domain.model.Contact
import com.YashDoesCode.comms.domain.repository.CallLogRepository
import com.YashDoesCode.comms.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeContactRepository : ContactRepository {
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())

    fun setContacts(contacts: List<Contact>) {
        _contacts.value = contacts
    }

    override fun getContacts(): Flow<List<Contact>> = _contacts

    override suspend fun searchContacts(query: String): List<Contact> {
        return if (query.isBlank()) {
            _contacts.value
        } else {
            _contacts.value.filter {
                it.name.contains(query, ignoreCase = true) || it.phoneNumber.contains(query, ignoreCase = true)
            }
        }
    }

    override suspend fun syncContacts(): Result<Unit> = Result.success(Unit)
}

class FakeCallLogRepository : CallLogRepository {
    private val _calls = MutableStateFlow<List<CallLogEntry>>(emptyList())

    fun setCalls(calls: List<CallLogEntry>) {
        _calls.value = calls
    }

    override fun getRecentCalls(): Flow<List<CallLogEntry>> = _calls

    override suspend fun fetchRecentCalls(): Result<List<CallLogEntry>> = Result.success(_calls.value)
}
