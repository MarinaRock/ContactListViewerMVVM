package ru.marina.contactlistviewermvvm.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.marina.contactlistviewermvvm.vm.AppViewModel
import ru.marina.contactlistviewermvvm.vm.contacts.ContactInfoViewModel
import ru.marina.contactlistviewermvvm.vm.contacts.ContactsViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AppViewModel::class)
    abstract fun bindAppViewModel(appViewModel: AppViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactsViewModel::class)
    abstract fun bindContactsViewModel(contactsViewModel: ContactsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactInfoViewModel::class)
    abstract fun bindContactInfoViewModel(contactInfoViewModel: ContactInfoViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}