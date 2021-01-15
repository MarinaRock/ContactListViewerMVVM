package ru.marina.contactlistviewermvvm.ui

import androidx.lifecycle.Observer

sealed class Event<out T> {

    open class Success<out T>(val data: T) : Event<T>()
    open class Error() : Event<Nothing>() {
        var msg: String? = null
            private set
        var exception: Throwable? = null
            private set

        constructor(msg: String) : this() {
            this.msg = msg
        }

        constructor(exception: Throwable) : this() {
            this.exception = exception
        }
    }

    class SingleSuccess<out T>(singleData: T) : Success<T>(singleData)
    class SingleError : Error {
        constructor(msg: String) : super(msg)
        constructor(exception: Throwable) : super(exception)
    }

    object Loading : Event<Nothing>()

    private var wasHandled = false

    fun getIfNotHandled(): Event<T>? {
        return if (wasHandled) {
            null
        } else {
            wasHandled = when (this) {
                is SingleSuccess -> true
                is SingleError -> true
                is Success -> false
                is Error -> false
                Loading -> false
            }
            this
        }
    }
}

class EventObserver<T>(
    private val onSuccess: (T) -> Unit = {},
    private val onError: (Event.Error) -> Unit = {},
    private val onLoading: () -> Unit = {}
) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getIfNotHandled()?.let { value ->
            when (value) {
                is Event.Success -> onSuccess(value.data)
                is Event.Error -> onError(value)
                Event.Loading -> onLoading()
            }
        }
    }
}
