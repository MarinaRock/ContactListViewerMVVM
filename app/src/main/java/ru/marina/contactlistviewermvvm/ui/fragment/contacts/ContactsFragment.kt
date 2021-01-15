package ru.marina.contactlistviewermvvm.ui.fragment.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import ru.marina.contactlistviewermvvm.databinding.FragmentContactsBinding
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.ui.EventObserver
import ru.marina.contactlistviewermvvm.ui.ViewEffect
import ru.marina.contactlistviewermvvm.ui.ViewEvent
import ru.marina.contactlistviewermvvm.ui.adapter.ContactsAdapter
import ru.marina.contactlistviewermvvm.ui.extensions.disable
import ru.marina.contactlistviewermvvm.ui.extensions.enable
import ru.marina.contactlistviewermvvm.ui.extensions.getMsgFromError
import ru.marina.contactlistviewermvvm.ui.extensions.showSnackBar
import ru.marina.contactlistviewermvvm.ui.fragment.base.BaseFragment
import ru.marina.contactlistviewermvvm.vm.contacts.ContactsViewModel
import javax.inject.Inject

class ContactsFragment : BaseFragment<FragmentContactsBinding>(),
    ContactsAdapter.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContactsBinding =
        FragmentContactsBinding::inflate

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ContactsViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var adapter: ContactsAdapter

    private var isContactsReloaded = true
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        adapter = ContactsAdapter()
        adapter.setOnItemClickListener(this)

        initObservers()

        viewModel.event(ViewEvent.ContactsLoad)
    }

    private fun initObservers() {
        viewModel.contactsState.observe(
            this,
            EventObserver(
                onSuccess = {
                    binding.searchView.enable()
                    render(it)
                    stopProgress()
                },
                onError = {
                    binding.searchView.enable()
                    stopProgress()
                    showSnackBar(getMsgFromError(it))
                },
                onLoading = {
                    if (isContactsReloaded) {
                        binding.searchView.disable()
                        isContactsReloaded = false
                    }
                    startProgress()
                })
        )

        viewModel.viewEffect.observe(
            this,
            EventObserver(
                { trigger(it) }
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener(this)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recycleView.layoutManager = linearLayoutManager
        binding.recycleView.adapter = adapter

        initSearchView()
    }

    private fun initSearchView() {
        val searchViewText: SearchView.SearchAutoComplete =
            binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchViewText.isSaveEnabled = false
        binding.searchView.setQuery(query, false)
        val observable: Observable<String> = Observable.create { emitter ->
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        emitter.onNext(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        emitter.onNext(newText)
                    }
                    return true
                }
            })
        }
        viewModel.searchContacts(observable)
    }

    private fun render(state: PagedList<Contact>) {
        adapter.submitList(state)
    }

    private fun trigger(effect: ViewEffect) {
        when (effect) {
            is ViewEffect.NavigateToContactInfoScreen -> {
                query = binding.searchView.query.toString()
                viewModel.showContactInfoScreen(effect.contactId)
            }
        }
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun onRefresh() {
        isContactsReloaded = true
        viewModel.event(ViewEvent.SwipeToRefresh)
    }

    override fun onItemClick(contact: Contact) {
        viewModel.event(ViewEvent.ContactClick(contact.id))
    }
}