package ru.marina.contactlistviewermvvm.ui.extensions

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.marina.contactlistviewermvvm.R
import ru.marina.contactlistviewermvvm.data.network.NoNetworkException
import ru.marina.contactlistviewermvvm.data.network.ServerException
import ru.marina.contactlistviewermvvm.ui.Event

fun Fragment.getMsgFromError(error: Event.Error): String {
    return error.msg ?: error.exception.let {
        when (it) {
            is ServerException -> it.message ?: getString(R.string.error_server)
            is NoNetworkException -> getString(R.string.error_network)
            else -> getString(R.string.error_unknown)
        }
    }
}

fun Fragment.showSnackBar(msg: String) {
    view?.let {
        Snackbar.make(it, msg, Snackbar.LENGTH_LONG).show()
    }
}
