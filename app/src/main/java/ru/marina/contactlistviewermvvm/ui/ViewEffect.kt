package ru.marina.contactlistviewermvvm.ui

sealed class ViewEffect {
    // Contacts
    data class NavigateToContactInfoScreen(val contactId: String) : ViewEffect()

    // Contact Info
    data class CallContactPhone(val contactPhone: String) : ViewEffect()
}