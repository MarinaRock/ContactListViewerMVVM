package ru.marina.contactlistviewermvvm.domain.usecase

import io.reactivex.Single
import ru.marina.contactlistviewermvvm.domain.executor.SchedulersProvider
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.domain.repository.ContactsRepository
import ru.marina.contactlistviewermvvm.domain.usecase.base.SingleUseCase

class ContactsWithCacheUseCase(
    private val contactsRepository: ContactsRepository,
    schedulersProvider: SchedulersProvider
) : SingleUseCase<Any, List<Contact>>(schedulersProvider) {

    override fun build(params: Any): Single<List<Contact>> =
        contactsRepository.getContactsWithCache()
}