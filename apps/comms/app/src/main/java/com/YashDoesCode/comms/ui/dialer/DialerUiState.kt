package com.YashDoesCode.comms.ui.dialer

import com.YashDoesCode.comms.domain.model.CallLogEntry
import com.YashDoesCode.comms.domain.model.Contact

enum class DialerSubTab {
    KEYPAD,
    RECENTS,
    CONTACTS
}

data class DialerUiState(
    val digits: String = "",
    val contacts: List<Contact> = emptyList(),
    val recentCalls: List<CallLogEntry> = emptyList(),
    val searchQuery: String = "",
    val activeSubTab: DialerSubTab = DialerSubTab.KEYPAD,
    val hasContactsPermission: Boolean = false,
    val hasCallLogPermission: Boolean = false,
    val isLoading: Boolean = false
) {
    val filteredContacts: List<Contact>
        get() = if (searchQuery.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.phoneNumber.contains(searchQuery, ignoreCase = true)
            }
        }
}

sealed interface DialerAction {
    data class AppendDigit(val digit: Char) : DialerAction
    data object DeleteDigit : DialerAction
    data object ClearDigits : DialerAction
    data class SetDigits(val digits: String) : DialerAction
    data class SelectSubTab(val tab: DialerSubTab) : DialerAction
    data class UpdateSearchQuery(val query: String) : DialerAction
    data class UpdatePermissions(val hasContacts: Boolean, val hasCallLog: Boolean) : DialerAction
    data object Refresh : DialerAction
}
