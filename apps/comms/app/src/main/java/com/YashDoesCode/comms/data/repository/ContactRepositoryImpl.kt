package com.YashDoesCode.comms.data.repository

import com.YashDoesCode.comms.data.datasource.ContactsDataSource
import com.YashDoesCode.comms.data.local.ContactDao
import com.YashDoesCode.comms.data.local.ContactEntity
import com.YashDoesCode.comms.domain.model.Contact
import com.YashDoesCode.comms.domain.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class ContactRepositoryImpl(
    private val contactDao: ContactDao,
    private val dataSource: ContactsDataSource
) : ContactRepository {

    override fun getContacts(): Flow<List<Contact>> {
        return contactDao.getContacts()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                syncContacts()
            }
    }

    override suspend fun searchContacts(query: String): List<Contact> = withContext(Dispatchers.IO) {
        contactDao.searchContacts(query).map { it.toDomain() }
    }

    override suspend fun syncContacts(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val freshContacts = dataSource.fetchContacts()
            if (freshContacts.isNotEmpty()) {
                contactDao.clearContacts()
                contactDao.insertContacts(freshContacts.map { ContactEntity.fromDomain(it) })
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
