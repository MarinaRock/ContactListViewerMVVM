package ru.marina.contactlistviewermvvm.vm.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableSingleObserver
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.domain.usecase.ContactUseCase
import ru.marina.contactlistviewermvvm.ui.Event
import ru.marina.contactlistviewermvvm.ui.SingleLiveEvent
import ru.marina.contactlistviewermvvm.ui.ViewEffect
import ru.marina.contactlistviewermvvm.ui.ViewEvent
import ru.marina.contactlistviewermvvm.vm.base.BaseViewModel
import javax.inject.Inject

class ContactInfoViewModel @Inject constructor(
    private val router: Router,
    private val contactUseCase: ContactUseCase,
) : BaseViewModel() {

    private val _contactState = MutableLiveData<Event<Contact>>()
    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val contactState: LiveData<Event<Contact>> = _contactState
    val viewEffect: LiveData<ViewEffect> = _viewEffect

    fun event(event: ViewEvent) {
        when (event) {
            is ViewEvent.ContactReceived -> getContact(event.contactId)
            is ViewEvent.ContactPhoneClick -> _viewEffect.value =
                ViewEffect.CallContactPhone(event.contactPhone)
        }
    }

    private fun getContact(id: String) {
        contactUseCase.execute(id, object : DisposableSingleObserver<Contact>() {
            override fun onSuccess(t: Contact) {
                _contactState.value = Event.Success(t)
            }

            override fun onError(e: Throwable) {
                _contactState.value = Event.SingleError(e)
            }
        })
            .disposeLater()
    }

    fun onBackPressed() {
        router.exit()
    }
}