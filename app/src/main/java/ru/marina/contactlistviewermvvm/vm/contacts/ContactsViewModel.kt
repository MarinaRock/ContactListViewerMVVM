package ru.marina.contactlistviewermvvm.vm.contacts

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import com.github.terrakok.cicerone.Router
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import ru.marina.contactlistviewermvvm.data.CONST.LIST_PAGE_SIZE
import ru.marina.contactlistviewermvvm.domain.executor.SchedulersProvider
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.domain.usecase.ContactsUseCase
import ru.marina.contactlistviewermvvm.domain.usecase.ContactsWithCacheUseCase
import ru.marina.contactlistviewermvvm.domain.usecase.SearchContactsUseCase
import ru.marina.contactlistviewermvvm.executor.MainThreadExecutor
import ru.marina.contactlistviewermvvm.navigation.Screens
import ru.marina.contactlistviewermvvm.ui.Event
import ru.marina.contactlistviewermvvm.ui.ViewEffect
import ru.marina.contactlistviewermvvm.ui.ViewEvent
import ru.marina.contactlistviewermvvm.vm.base.BaseViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ContactsViewModel @Inject constructor(
    private val router: Router,
    private val schedulersProvider: SchedulersProvider,
    private val contactsWithCacheUseCase: ContactsWithCacheUseCase,
    private val contactsUseCase: ContactsUseCase,
    private val searchContactsUseCase: SearchContactsUseCase
) : BaseViewModel() {

    val contactsState: MutableLiveData<Event<PagedList<Contact>>> = MutableLiveData()
    val viewEffect: MutableLiveData<Event<ViewEffect>> = MutableLiveData()

    fun event(event: ViewEvent) {
        when (event) {
            is ViewEvent.ContactsLoad -> getContactsWithCache()
            is ViewEvent.SwipeToRefresh -> getContacts()
            is ViewEvent.ContactClick -> viewEffect.value =
                Event.SingleSuccess(ViewEffect.NavigateToContactInfoScreen(event.contactId))
        }
    }

    private fun getContactsWithCache() {
        contactsState.value = Event.Loading
        contactsWithCacheUseCase.execute(Any(), getContactsSingleObserver())
    }

    private fun getContacts() {
        contactsState.value = Event.Loading
        contactsUseCase.execute(Any(), getContactsSingleObserver())
    }

    private fun getContactsSingleObserver() = object : DisposableSingleObserver<List<Contact>>() {
        override fun onSuccess(t: List<Contact>) {
            contactsState.value = Event.Success(getPagingContactsList(t))
        }

        override fun onError(e: Throwable) {
            contactsState.value = Event.SingleError(e)
        }
    }

    private fun getPagingContactsList(contacts: List<Contact>): PagedList<Contact> {
        val pagedConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(LIST_PAGE_SIZE)
            .build()

        return PagedList.Builder(object : PositionalDataSource<Contact>() {
            override fun loadInitial(
                params: LoadInitialParams,
                callback: LoadInitialCallback<Contact>
            ) {
                callback.onResult(
                    contacts.subList(
                        params.requestedStartPosition,
                        if (contacts.size < params.requestedLoadSize) contacts.size else params.requestedLoadSize
                    ), 0
                )
            }

            override fun loadRange(
                params: LoadRangeParams,
                callback: LoadRangeCallback<Contact>
            ) {
                callback.onResult(
                    contacts.subList(
                        params.startPosition,
                        if (params.loadSize + params.startPosition >= contacts.size) contacts.size else params.loadSize + params.startPosition
                    )
                )
            }
        }, pagedConfig)
            .setNotifyExecutor(MainThreadExecutor())
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    fun searchContacts(observable: Observable<String>) {
        val disposable: Disposable = observable
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .map {
                contactsState.postValue(Event.Loading)
                return@map it
            }
            .switchMap {
                searchContactsUseCase.execute(it)
            }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribeWith(object : DisposableObserver<List<Contact>>() {
                override fun onNext(t: List<Contact>) {
                    contactsState.value = Event.Success(getPagingContactsList(t))
                }

                override fun onComplete() {
                }

                override fun onError(e: Throwable) {
                    contactsState.value = Event.SingleError(e)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun showContactInfoScreen(contactId: String) {
        router.navigateTo(Screens.ContactInfoScreen(contactId))
    }

    fun onBackPressed() {
        router.exit()
    }
}