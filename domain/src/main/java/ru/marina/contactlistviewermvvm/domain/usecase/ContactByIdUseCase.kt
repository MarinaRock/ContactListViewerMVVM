package ru.marina.contactlistviewermvvm.domain.usecase

import io.reactivex.Single
import ru.marina.contactlistviewermvvm.domain.executor.SchedulersProvider
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.domain.repository.ContactsRepository
import ru.marina.contactlistviewermvvm.domain.usecase.base.SingleUseCase

class ContactByIdUseCase(
    private val contactsRepository: ContactsRepository,
    schedulersProvider: SchedulersProvider
) : SingleUseCase<String, Contact>(schedulersProvider) {

    override fun build(params: String): Single<Contact> = contactsRepository.getContactById(params)
}