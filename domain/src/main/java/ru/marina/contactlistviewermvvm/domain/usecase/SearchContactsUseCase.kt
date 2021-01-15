package ru.marina.contactlistviewermvvm.domain.usecase

import io.reactivex.Observable
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.domain.repository.ContactsRepository
import ru.marina.contactlistviewermvvm.domain.usecase.base.ObservableUseCase

class SearchContactsUseCase(
    private val contactsRepository: ContactsRepository,
) : ObservableUseCase<String, List<Contact>> {

    override fun execute(params: String): Observable<List<Contact>> =
        contactsRepository.getSearchContacts(params).toObservable()
}