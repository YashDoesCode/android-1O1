package com.YashDoesCode.comms.data.datasource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.YashDoesCode.comms.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsDataSource(private val context: Context) {

    suspend fun fetchContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return@withContext getSampleContacts()
        }

        val contactsList = mutableListOf<Contact>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        )

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                val lookupCol = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val name = it.getString(nameCol) ?: "Unknown"
                    val number = it.getString(numberCol) ?: ""
                    val photoUri = it.getString(photoCol)
                    val lookupKey = it.getString(lookupCol)

                    contactsList.add(
                        Contact(
                            id = id,
                            name = name,
                            phoneNumber = number,
                            photoUri = photoUri,
                            lookupKey = lookupKey
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Fallback gracefully on emulator if contacts provider is unavailable
            return@withContext getSampleContacts()
        }

        if (contactsList.isEmpty()) {
            getSampleContacts()
        } else {
            contactsList
        }
    }

    private fun getSampleContacts(): List<Contact> = listOf(
        Contact(1L, "Alice Johnson", "+1 (555) 123-4567", "alice@example.com"),
        Contact(2L, "Bob Smith", "+1 (555) 234-5678", "bob@example.com"),
        Contact(3L, "Charlie Brown", "+1 (555) 345-6789", "charlie@example.com"),
        Contact(4L, "Diana Prince", "+1 (555) 456-7890", "diana@example.com"),
        Contact(5L, "Evan Wright", "+1 (555) 567-8901", "evan@example.com")
    )
}
