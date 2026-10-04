package com.YashDoesCode.comms.ui

import com.YashDoesCode.comms.domain.FakeCallLogRepository
import com.YashDoesCode.comms.domain.FakeContactRepository
import com.YashDoesCode.comms.domain.model.Contact
import com.YashDoesCode.comms.domain.usecase.FetchRecentCallsUseCase
import com.YashDoesCode.comms.domain.usecase.GetContactsUseCase
import com.YashDoesCode.comms.domain.usecase.GetRecentCallsUseCase
import com.YashDoesCode.comms.domain.usecase.SyncContactsUseCase
import com.YashDoesCode.comms.ui.dialer.DialerAction
import com.YashDoesCode.comms.ui.dialer.DialerSubTab
import com.YashDoesCode.comms.ui.dialer.DialerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DialerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var contactRepository: FakeContactRepository
    private lateinit var callLogRepository: FakeCallLogRepository
    private lateinit var viewModel: DialerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        contactRepository = FakeContactRepository()
        callLogRepository = FakeCallLogRepository()
        viewModel = DialerViewModel(
            getContactsUseCase = GetContactsUseCase(contactRepository),
            syncContactsUseCase = SyncContactsUseCase(contactRepository),
            getRecentCallsUseCase = GetRecentCallsUseCase(callLogRepository),
            fetchRecentCallsUseCase = FetchRecentCallsUseCase(callLogRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun appendAndManipulateDigits_updatesUiState() {
        viewModel.onAction(DialerAction.AppendDigit('1'))
        viewModel.onAction(DialerAction.AppendDigit('2'))
        viewModel.onAction(DialerAction.AppendDigit('3'))
        assertEquals("123", viewModel.uiState.value.digits)

        viewModel.onAction(DialerAction.DeleteDigit)
        assertEquals("12", viewModel.uiState.value.digits)

        viewModel.onAction(DialerAction.AppendDigit('+'))
        assertEquals("12+", viewModel.uiState.value.digits)

        viewModel.onAction(DialerAction.ClearDigits)
        assertEquals("", viewModel.uiState.value.digits)
    }

    @Test
    fun selectSubTab_updatesActiveTab() {
        viewModel.onAction(DialerAction.SelectSubTab(DialerSubTab.RECENTS))
        assertEquals(DialerSubTab.RECENTS, viewModel.uiState.value.activeSubTab)

        viewModel.onAction(DialerAction.SelectSubTab(DialerSubTab.CONTACTS))
        assertEquals(DialerSubTab.CONTACTS, viewModel.uiState.value.activeSubTab)
    }

    @Test
    fun searchContacts_filtersContactsList() = runTest {
        val contacts = listOf(
            Contact(1L, "John Doe", "12345"),
            Contact(2L, "Jane Smith", "67890")
        )
        contactRepository.setContacts(contacts)
        testScheduler.advanceUntilIdle()

        viewModel.onAction(DialerAction.UpdateSearchQuery("Jane"))
        assertEquals("Jane", viewModel.uiState.value.searchQuery)
        val filtered = viewModel.uiState.value.filteredContacts
        assertEquals(1, filtered.size)
        assertEquals("Jane Smith", filtered[0].name)
    }

    @Test
    fun updatePermissions_updatesPermissionFlags() {
        viewModel.onAction(DialerAction.UpdatePermissions(hasContacts = true, hasCallLog = false))
        assertTrue(viewModel.uiState.value.hasContactsPermission)
        assertEquals(false, viewModel.uiState.value.hasCallLogPermission)
    }
}
