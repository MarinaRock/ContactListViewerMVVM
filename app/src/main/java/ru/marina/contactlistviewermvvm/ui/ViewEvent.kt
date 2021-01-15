package ru.marina.contactlistviewermvvm.ui

sealed class ViewEvent {
    // Contacts
    object ContactsLoad : ViewEvent()
    object SwipeToRefresh : ViewEvent()
    data class ContactClick(val contactId: String) : ViewEvent()

    // Contact Info
    data class ContactReceived(val contactId: String) : ViewEvent()
    data class ContactPhoneClick(val contactPhone: String) : ViewEvent()
}