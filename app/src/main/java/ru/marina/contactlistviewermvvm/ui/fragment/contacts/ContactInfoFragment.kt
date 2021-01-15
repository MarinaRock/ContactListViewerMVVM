package ru.marina.contactlistviewermvvm.ui.fragment.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import ru.marina.contactlistviewermvvm.databinding.FragmentContactInfoBinding
import ru.marina.contactlistviewermvvm.domain.model.Contact
import ru.marina.contactlistviewermvvm.ui.EventObserver
import ru.marina.contactlistviewermvvm.ui.ViewEffect
import ru.marina.contactlistviewermvvm.ui.ViewEvent
import ru.marina.contactlistviewermvvm.ui.extensions.getMsgFromError
import ru.marina.contactlistviewermvvm.ui.extensions.showSnackBar
import ru.marina.contactlistviewermvvm.ui.fragment.base.BaseFragment
import ru.marina.contactlistviewermvvm.util.DateUtils
import ru.marina.contactlistviewermvvm.util.Intents
import ru.marina.contactlistviewermvvm.vm.contacts.ContactInfoViewModel
import javax.inject.Inject

class ContactInfoFragment : BaseFragment<FragmentContactInfoBinding>() {

    companion object {
        private const val PERMISSION_CALL_PHONE = 1111

        private const val EXTRA_CONTACT_ID = "EXTRA_CONTACT_ID"

        fun getInstance(id: String) =
            ContactInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_CONTACT_ID, id)
                }
            }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContactInfoBinding =
        FragmentContactInfoBinding::inflate

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ContactInfoViewModel by viewModels {
        viewModelFactory
    }

    lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        val contactId: String = arguments?.getString(EXTRA_CONTACT_ID, "") ?: ""

        initObservers()

        viewModel.event(ViewEvent.ContactReceived(contactId))
    }

    private fun initObservers() {
        viewModel.contactState.observe(
            this,
            EventObserver(
                onSuccess = {
                    render(it)
                },
                onError = {
                    showSnackBar(getMsgFromError(it))
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

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.contactPhoneTextView.setOnClickListener {
            viewModel.event(ViewEvent.ContactPhoneClick(contact.phone))
        }
    }

    private fun render(state: Contact) {
        binding.contactNameTextView.text = state.name
        binding.contactPhoneTextView.text = state.phone
        binding.contactTemperamentTextView.text = state.temperament.name.capitalize()
        val period = DateUtils.getStandardDate(state.educationPeriod.start) +
                " - " +
                DateUtils.getStandardDate(state.educationPeriod.end)
        binding.contactEducationDatesTextView.text = period
        binding.contactBiographyTextView.text = state.biography

        contact = state
    }

    private fun trigger(effect: ViewEffect) {
        when (effect) {
            is ViewEffect.CallContactPhone -> {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Intents.callPhone(requireContext(), effect.contactPhone)
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.CALL_PHONE),
                        PERMISSION_CALL_PHONE
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intents.callPhone(requireContext(), contact.phone)
                } else {
                    Intents.openPermissionsSetting(requireContext())
                }
            }
        }
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
}