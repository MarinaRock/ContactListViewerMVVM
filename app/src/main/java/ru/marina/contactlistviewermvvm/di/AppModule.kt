package ru.marina.contactlistviewermvvm.di

import dagger.Module
import dagger.Provides
import ru.marina.contactlistviewermvvm.domain.executor.SchedulersProvider
import ru.marina.contactlistviewermvvm.domain.repository.ContactsRepository
import ru.marina.contactlistviewermvvm.domain.usecase.ContactByIdUseCase
import ru.marina.contactlistviewermvvm.domain.usecase.ContactsUseCase
import ru.marina.contactlistviewermvvm.domain.usecase.ContactsWithCacheUseCase
import ru.marina.contactlistviewermvvm.domain.usecase.SearchContactsUseCase
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideContactsUseCase(
        contactsRepository: ContactsRepository,
        schedulersProvider: SchedulersProvider
    ): ContactsUseCase = ContactsUseCase(contactsRepository, schedulersProvider)

    @Singleton
    @Provides
    fun provideContactsWithCacheUseCase(
        contactsRepository: ContactsRepository,
        schedulersProvider: SchedulersProvider
    ): ContactsWithCacheUseCase = ContactsWithCacheUseCase(contactsRepository, schedulersProvider)

    @Singleton
    @Provides
    fun provideSearchContactsUseCase(
        contactsRepository: ContactsRepository
    ): SearchContactsUseCase = SearchContactsUseCase(contactsRepository)

    @Singleton
    @Provides
    fun provideContactByIdUseCase(
        contactsRepository: ContactsRepository,
        schedulersProvider: SchedulersProvider
    ): ContactByIdUseCase = ContactByIdUseCase(contactsRepository, schedulersProvider)
}