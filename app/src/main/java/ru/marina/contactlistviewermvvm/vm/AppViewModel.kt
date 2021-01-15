package ru.marina.contactlistviewermvvm.vm

import com.github.terrakok.cicerone.Router
import ru.marina.contactlistviewermvvm.navigation.Screens
import ru.marina.contactlistviewermvvm.vm.base.BaseViewModel
import javax.inject.Inject

class AppViewModel @Inject constructor(
    private val router: Router
) : BaseViewModel() {

    fun startNavigation() {
        router.newRootScreen(Screens.ContactsScreen())
    }

    fun onBackPressed() {
        router.exit()
    }
}