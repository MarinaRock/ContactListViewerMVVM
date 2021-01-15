package ru.marina.contactlistviewermvvm.data.remote

import io.reactivex.Single
import ru.marina.contactlistviewermvvm.data.mapper.ContactsMapper
import ru.marina.contactlistviewermvvm.data.network.api.ApiService
import ru.marina.contactlistviewermvvm.domain.model.Contact
import javax.inject.Inject

class ContactsRemoteImpl @Inject constructor(
    private val service: ApiService,
    private val contactsMapper: ContactsMapper
) : ContactsRemote {

    override fun getContacts(source: String): Single<List<Contact>> =
        service.getContacts(source)
            .map { contactsMapper.mapFromResponseToModelList(it) }
}