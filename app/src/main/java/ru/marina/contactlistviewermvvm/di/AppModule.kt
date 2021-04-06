package ru.marina.contactlistviewermvvm.di

import dagger.Module
import dagger.Provides
import ru.marina.contactlistviewermvvm.domain.executor.SchedulersProvider
import ru.marina.contactlistviewermvvm.domain.repository.ContactsRepository
import ru.marina.contactlistviewermvvm.domain.usecase.ContactUseCase
import ru.marina.contactlistviewermvvm.domain.usecase.ContactsUseCase
import ru.marina.contactlistviewermvvm.domain.usecase.ContactsWithCacheUseCase
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
    fun provideContactUseCase(
        contactsRepository: ContactsRepository,
        schedulersProvider: SchedulersProvider
    ): ContactUseCase = ContactUseCase(contactsRepository, schedulersProvider)
}