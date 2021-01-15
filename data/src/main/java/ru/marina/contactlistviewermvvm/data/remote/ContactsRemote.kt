package ru.marina.contactlistviewermvvm.data.remote

import io.reactivex.Single
import ru.marina.contactlistviewermvvm.domain.model.Contact

interface ContactsRemote {

    fun getContacts(source: String): Single<List<Contact>>
}