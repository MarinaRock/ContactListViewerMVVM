package ru.marina.contactlistviewermvvm.domain.usecase.base

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import ru.marina.contactlistviewermvvm.domain.executor.SchedulersProvider

abstract class SingleUseCase<P, R>(private val schedulersProvider: SchedulersProvider) {

    abstract fun build(params: P): Single<R>

    fun execute(params: P, observer: DisposableSingleObserver<R>): Disposable =
        build(params)
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribeWith(observer)
}