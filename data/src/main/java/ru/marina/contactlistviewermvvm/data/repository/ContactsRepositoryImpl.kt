package ru.marina.contactlistviewermvvm.data.repository

import io.reactivex.Single
import io.reactivex.functions.Function3
import ru.marina.contactlistviewermvvm.data.CONST.CACHE_UPDATE_TIME
import ru.marina.contactlistviewermvvm.data.db.dao.ContactsDao
import ru.marina.contactlistviewermvvm.data.mapper.ContactsMapper
import ru.marina.contactlistviewermvvm.data.prefs.Prefs
import ru.marina.contactlistviewermvvm.data.remote.ContactsRemote
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.domain.repository.ContactsRepository
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val contactsMapper: ContactsMapper,
    private val contactsRemote: ContactsRemote,
    private val contactsDao: ContactsDao,
    private val prefs: Prefs
) : ContactsRepository {

    override fun getContactsWithCache(): Single<List<Contact>> =
        if (System.currentTimeMillis() - prefs.lastCacheTime >= CACHE_UPDATE_TIME) {
            getContacts()
        } else {
            getContactsFromCache()
        }

    override fun getContacts(): Single<List<Contact>> = getContactsFromNetwork()

    override fun getSearchContacts(query: String): Single<List<Contact>> =
        getContactsFromCache().map { contacts ->
            contacts.filter {
                val queryLowerCase = query.lowercase().trim()
                val phone = it.phone.replace(Regex("\\D"), "")
                val name = it.name

                name.lowercase().contains(queryLowerCase) ||
                        phone.contains(queryLowerCase) ||
                        it.phone.contains(queryLowerCase)
            }
        }

    override fun getContact(id: String): Single<Contact> =
        contactsDao.getContact(id)
            .map { contactsMapper.mapFromDbToModel(it) }

    private fun getContactsFromNetwork(): Single<List<Contact>> =
        Single.zip(
            contactsRemote.getContacts("generated-01.json"),
            contactsRemote.getContacts("generated-02.json"),
            contactsRemote.getContacts("generated-03.json"),
            Function3<List<Contact>, List<Contact>, List<Contact>, List<Contact>>
            { contacts1, contacts2, contacts3 ->
                val allContacts: MutableList<Contact> = mutableListOf()

                allContacts.addAll(contacts1)
                allContacts.addAll(contacts2)
                allContacts.addAll(contacts3)

                contactsDao.updateContacts(contactsMapper.mapFromModelToDbList(allContacts))

                prefs.lastCacheTime = System.currentTimeMillis()

                return@Function3 allContacts
            }
        )
            .map { contacts -> contacts.sortedBy { it.name } }

    private fun getContactsFromCache(): Single<List<Contact>> =
        contactsDao.getContacts()
            .map { contactsMapper.mapFromDbToModelList(it) }
            .map { contacts -> contacts.sortedBy { it.name } }
}