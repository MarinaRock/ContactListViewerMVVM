package ru.marina.contactlistviewermvvm.vm.contacts

import androidx.lifecycle.MutableLiveData
import com.github.terrakok.cicerone.Router
import io.reactivex.observers.DisposableSingleObserver
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.domain.usecase.ContactByIdUseCase
import ru.marina.contactlistviewermvvm.ui.Event
import ru.marina.contactlistviewermvvm.ui.ViewEffect
import ru.marina.contactlistviewermvvm.ui.ViewEvent
import ru.marina.contactlistviewermvvm.vm.base.BaseViewModel
import javax.inject.Inject

class ContactInfoViewModel @Inject constructor(
    private val router: Router,
    private val contactByIdUseCase: ContactByIdUseCase,
) : BaseViewModel() {

    val contactState: MutableLiveData<Event<Contact>> = MutableLiveData()
    val viewEffect: MutableLiveData<Event<ViewEffect>> = MutableLiveData()

    fun event(event: ViewEvent) {
        when (event) {
            is ViewEvent.ContactReceived -> getContactById(event.contactId)
            is ViewEvent.ContactPhoneClick -> viewEffect.value =
                Event.SingleSuccess(ViewEffect.CallContactPhone(event.contactPhone))
        }
    }

    private fun getContactById(id: String) {
        contactByIdUseCase.execute(id, object : DisposableSingleObserver<Contact>() {
            override fun onSuccess(t: Contact) {
                contactState.value = Event.Success(t)
            }

            override fun onError(e: Throwable) {
                contactState.value = Event.SingleError(e)
            }
        })
    }

    fun onBackPressed() {
        router.exit()
    }
}