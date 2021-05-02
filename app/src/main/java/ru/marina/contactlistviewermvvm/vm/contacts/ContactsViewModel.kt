package ru.marina.contactlistviewermvvm.vm.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import com.github.terrakok.cicerone.Router
import io.reactivex.Flowable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber
import ru.marina.contactlistviewermvvm.data.CONST.LIST_PAGE_SIZE
import ru.marina.contactlistviewermvvm.domain.executor.SchedulersProvider
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.domain.repository.ContactsRepository
import ru.marina.contactlistviewermvvm.domain.usecase.ContactsUseCase
import ru.marina.contactlistviewermvvm.domain.usecase.ContactsWithCacheUseCase
import ru.marina.contactlistviewermvvm.executor.MainThreadExecutor
import ru.marina.contactlistviewermvvm.navigation.Screens
import ru.marina.contactlistviewermvvm.ui.Event
import ru.marina.contactlistviewermvvm.ui.SingleLiveEvent
import ru.marina.contactlistviewermvvm.ui.ViewEffect
import ru.marina.contactlistviewermvvm.ui.ViewEvent
import ru.marina.contactlistviewermvvm.vm.base.BaseViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ContactsViewModel @Inject constructor(
    private val router: Router,
    private val schedulersProvider: SchedulersProvider,
    private val contactsRepository: ContactsRepository,
    private val contactsWithCacheUseCase: ContactsWithCacheUseCase,
    private val contactsUseCase: ContactsUseCase
) : BaseViewModel() {

    private val _contactsState = MutableLiveData<Event<PagedList<Contact>>>()
    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val contactsState: LiveData<Event<PagedList<Contact>>> = _contactsState
    val viewEffect: LiveData<ViewEffect> = _viewEffect

    fun event(event: ViewEvent) {
        when (event) {
            is ViewEvent.ContactsLoad -> getContactsWithCache()
            is ViewEvent.SwipeToRefresh -> getContacts()
            is ViewEvent.ContactClick -> _viewEffect.value =
                ViewEffect.NavigateToContactInfoScreen(event.contactId)
        }
    }

    private fun getContactsWithCache() {
        _contactsState.value = Event.Loading
        contactsWithCacheUseCase.execute(Any(), getContactsSingleObserver())
            .disposeLater()
    }

    private fun getContacts() {
        _contactsState.value = Event.Loading
        contactsUseCase.execute(Any(), getContactsSingleObserver())
    }

    private fun getContactsSingleObserver() = object : DisposableSingleObserver<List<Contact>>() {
        override fun onSuccess(t: List<Contact>) {
            _contactsState.value = Event.Success(getPagingContactsList(t))
        }

        override fun onError(e: Throwable) {
            _contactsState.value = Event.SingleError(e)
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

    fun searchContacts(flowable: Flowable<String>) {
        flowable
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .map {
                _contactsState.postValue(Event.Loading)
                return@map it
            }
            .switchMap {
                contactsRepository.getSearchContacts(it).toFlowable()
            }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribeWith(object : DisposableSubscriber<List<Contact>>() {
                override fun onNext(t: List<Contact>) {
                    _contactsState.value = Event.Success(getPagingContactsList(t))
                }

                override fun onComplete() {
                }

                override fun onError(e: Throwable) {
                    _contactsState.value = Event.SingleError(e)
                }
            })
            .disposeLater()
    }

    fun showContactInfoScreen(contactId: String) {
        router.navigateTo(Screens.ContactInfoScreen(contactId))
    }

    fun onBackPressed() {
        router.exit()
    }
}