package ru.marina.contactlistviewermvvm.domain.repository

import io.reactivex.Single
import ru.marina.contactlistviewermvvm.domain.model.Contact

interface ContactsRepository {
    fun getContactsWithCache(): Single<List<Contact>>
    fun getContacts(): Single<List<Contact>>
    fun getSearchContacts(query: String): Single<List<Contact>>
    fun getContactById(id: String): Single<Contact>
}