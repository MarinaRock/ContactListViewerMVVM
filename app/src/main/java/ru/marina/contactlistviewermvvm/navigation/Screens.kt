package ru.marina.contactlistviewermvvm.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.marina.contactlistviewermvvm.ui.fragment.contacts.ContactInfoFragment
import ru.marina.contactlistviewermvvm.ui.fragment.contacts.ContactsFragment

object Screens {

    fun ContactsScreen() = FragmentScreen(ContactsFragment::class.java.name) {
        ContactsFragment()
    }

    fun ContactInfoScreen(contactId: String) =
        FragmentScreen(ContactInfoFragment::class.java.name) {
            ContactInfoFragment.getInstance(contactId)
        }
}