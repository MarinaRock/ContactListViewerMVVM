package ru.marina.contactlistviewermvvm.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.marina.contactlistviewermvvm.ui.fragment.contacts.ContactInfoFragment
import ru.marina.contactlistviewermvvm.ui.fragment.contacts.ContactsFragment

object Screens {

    fun ContactsScreen() = FragmentScreen {
        ContactsFragment()
    }

    fun ContactInfoScreen(contactId: String) = FragmentScreen {
        ContactInfoFragment.getInstance(contactId)
    }
}