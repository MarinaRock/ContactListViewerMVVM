package ru.marina.contactlistviewermvvm.vm.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        compositeDisposable.dispose()
    }

    protected fun Disposable.disposeLater() {
        compositeDisposable.add(this)
    }
}