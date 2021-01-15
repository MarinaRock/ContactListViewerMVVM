package ru.marina.contactlistviewermvvm.data.mapper

import ru.marina.contactlistviewermvvm.data.db.entity.ContactDb
import ru.marina.contactlistviewermvvm.data.reponse.ContactResponse
import ru.marina.contactlistviewermvvm.domain.model.Contact

interface ContactsMapper {
    fun mapFromResponseToModel(data: ContactResponse): Contact
    fun mapFromResponseToModelList(data: List<ContactResponse>): List<Contact>
    fun mapFromModelToDb(data: Contact): ContactDb
    fun mapFromModelToDbList(data: List<Contact>): List<ContactDb>
    fun mapFromDbToModel(data: ContactDb): Contact
    fun mapFromDbToModelList(data: List<ContactDb>): List<Contact>
}