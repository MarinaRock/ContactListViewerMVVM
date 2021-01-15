package ru.marina.contactlistviewermvvm.domain.usecase.base

import io.reactivex.Observable

interface ObservableUseCase<P, R> {

    fun execute(params: P): Observable<R>
}