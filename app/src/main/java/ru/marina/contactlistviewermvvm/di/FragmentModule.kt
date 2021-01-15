package ru.marina.contactlistviewermvvm.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.marina.contactlistviewermvvm.ui.fragment.contacts.ContactInfoFragment
import ru.marina.contactlistviewermvvm.ui.fragment.contacts.ContactsFragment

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeContactsFragment(): ContactsFragment

    @ContributesAndroidInjector
    abstract fun contributeContactInfoFragment(): ContactInfoFragment
}