package com.YashDoesCode.comms.di

import android.content.Context
import com.YashDoesCode.comms.data.datasource.CallLogDataSource
import com.YashDoesCode.comms.data.datasource.ContactsDataSource
import com.YashDoesCode.comms.data.datasource.InMemoryEmailDataSource
import com.YashDoesCode.comms.data.local.CommsDatabase
import com.YashDoesCode.comms.data.repository.CallLogRepositoryImpl
import com.YashDoesCode.comms.data.repository.ContactRepositoryImpl
import com.YashDoesCode.comms.data.repository.EmailRepositoryImpl
import com.YashDoesCode.comms.domain.repository.CallLogRepository
import com.YashDoesCode.comms.domain.repository.ContactRepository
import com.YashDoesCode.comms.domain.repository.EmailRepository
import com.YashDoesCode.comms.domain.usecase.*

class CommsContainer(private val context: Context) {

    private val database: CommsDatabase by lazy {
        CommsDatabase.getInstance(context)
    }

    private val contactsDataSource by lazy { ContactsDataSource(context) }
    private val callLogDataSource by lazy { CallLogDataSource(context) }
    private val emailDataSource by lazy { InMemoryEmailDataSource() }

    val contactRepository: ContactRepository by lazy {
        ContactRepositoryImpl(database.contactDao(), contactsDataSource)
    }

    val callLogRepository: CallLogRepository by lazy {
        CallLogRepositoryImpl(callLogDataSource)
    }

    val emailRepository: EmailRepository by lazy {
        EmailRepositoryImpl(emailDataSource)
    }

    val getContactsUseCase by lazy { GetContactsUseCase(contactRepository) }
    val searchContactsUseCase by lazy { SearchContactsUseCase(contactRepository) }
    val syncContactsUseCase by lazy { SyncContactsUseCase(contactRepository) }
    val getRecentCallsUseCase by lazy { GetRecentCallsUseCase(callLogRepository) }
    val fetchRecentCallsUseCase by lazy { FetchRecentCallsUseCase(callLogRepository) }

    val getEmailThreadsUseCase by lazy { GetEmailThreadsUseCase(emailRepository) }
    val getEmailThreadByIdUseCase by lazy { GetEmailThreadByIdUseCase(emailRepository) }
    val sendEmailUseCase by lazy { SendEmailUseCase(emailRepository) }
    val markThreadAsReadUseCase by lazy { MarkThreadAsReadUseCase(emailRepository) }
    val toggleStarThreadUseCase by lazy { ToggleStarThreadUseCase(emailRepository) }
    val deleteEmailThreadUseCase by lazy { DeleteEmailThreadUseCase(emailRepository) }
}
