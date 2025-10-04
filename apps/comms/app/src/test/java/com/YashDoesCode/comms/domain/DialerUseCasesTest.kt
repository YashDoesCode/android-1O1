package com.YashDoesCode.comms.domain

import com.YashDoesCode.comms.domain.model.CallLogEntry
import com.YashDoesCode.comms.domain.model.CallType
import com.YashDoesCode.comms.domain.model.Contact
import com.YashDoesCode.comms.domain.usecase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DialerUseCasesTest {

    private lateinit var contactRepository: FakeContactRepository
    private lateinit var callLogRepository: FakeCallLogRepository
    private lateinit var getContactsUseCase: GetContactsUseCase
    private lateinit var searchContactsUseCase: SearchContactsUseCase
    private lateinit var getRecentCallsUseCase: GetRecentCallsUseCase

    @Before
    fun setUp() {
        contactRepository = FakeContactRepository()
        callLogRepository = FakeCallLogRepository()
        getContactsUseCase = GetContactsUseCase(contactRepository)
        searchContactsUseCase = SearchContactsUseCase(contactRepository)
        getRecentCallsUseCase = GetRecentCallsUseCase(callLogRepository)
    }

    @Test
    fun getContactsUseCase_returnsContacts() = runTest {
        val sampleContacts = listOf(
            Contact(1L, "Alice Smith", "+1234567890"),
            Contact(2L, "Bob Jones", "+1987654321")
        )
        contactRepository.setContacts(sampleContacts)

        val allContacts = getContactsUseCase().first()
        assertEquals(2, allContacts.size)
        assertEquals("Alice Smith", allContacts[0].name)
    }

    @Test
    fun searchContactsUseCase_filtersByQueryCorrectly() = runTest {
        val sampleContacts = listOf(
            Contact(1L, "Alice Smith", "+1234567890"),
            Contact(2L, "Bob Jones", "+1987654321")
        )
        contactRepository.setContacts(sampleContacts)

        val filtered = searchContactsUseCase("Alice")
        assertEquals(1, filtered.size)
        assertEquals("Alice Smith", filtered[0].name)
    }

    @Test
    fun getRecentCallsUseCase_returnsRecentCalls() = runTest {
        val sampleCalls = listOf(
            CallLogEntry(1L, "+1234567890", "Alice", CallType.INCOMING, System.currentTimeMillis(), 45L),
            CallLogEntry(2L, "+1987654321", "Bob", CallType.MISSED, System.currentTimeMillis(), 0L)
        )
        callLogRepository.setCalls(sampleCalls)

        val calls = getRecentCallsUseCase().first()
        assertEquals(2, calls.size)
        assertEquals("Alice", calls[0].callerName)
    }
}
