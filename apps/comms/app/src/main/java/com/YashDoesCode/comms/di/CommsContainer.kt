package com.YashDoesCode.comms.di

import android.content.Context
import com.YashDoesCode.comms.data.datasource.CallLogDataSource
import com.YashDoesCode.comms.data.datasource.ContactsDataSource
import com.YashDoesCode.comms.data.local.CommsDatabase
import com.YashDoesCode.comms.data.repository.CallLogRepositoryImpl
import com.YashDoesCode.comms.data.repository.ContactRepositoryImpl
import com.YashDoesCode.comms.domain.repository.CallLogRepository
import com.YashDoesCode.comms.domain.repository.ContactRepository
import com.YashDoesCode.comms.domain.usecase.*

class CommsContainer(private val context: Context) {

    private val database: CommsDatabase by lazy {
        CommsDatabase.getInstance(context)
    }

    private val contactsDataSource by lazy { ContactsDataSource(context) }
    private val callLogDataSource by lazy { CallLogDataSource(context) }

    val contactRepository: ContactRepository by lazy {
        ContactRepositoryImpl(database.contactDao(), contactsDataSource)
    }

    val callLogRepository: CallLogRepository by lazy {
        CallLogRepositoryImpl(callLogDataSource)
    }

    val getContactsUseCase by lazy { GetContactsUseCase(contactRepository) }
    val searchContactsUseCase by lazy { SearchContactsUseCase(contactRepository) }
    val syncContactsUseCase by lazy { SyncContactsUseCase(contactRepository) }
    val getRecentCallsUseCase by lazy { GetRecentCallsUseCase(callLogRepository) }
    val fetchRecentCallsUseCase by lazy { FetchRecentCallsUseCase(callLogRepository) }
}
