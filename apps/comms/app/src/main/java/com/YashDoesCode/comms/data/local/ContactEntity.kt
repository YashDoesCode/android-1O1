package com.YashDoesCode.comms.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.YashDoesCode.comms.domain.model.Contact

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val photoUri: String? = null,
    val lookupKey: String? = null
) {
    fun toDomain(): Contact = Contact(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        email = email,
        photoUri = photoUri,
        lookupKey = lookupKey
    )

    companion object {
        fun fromDomain(contact: Contact): ContactEntity = ContactEntity(
            id = contact.id,
            name = contact.name,
            phoneNumber = contact.phoneNumber,
            email = contact.email,
            photoUri = contact.photoUri,
            lookupKey = contact.lookupKey
        )
    }
}
