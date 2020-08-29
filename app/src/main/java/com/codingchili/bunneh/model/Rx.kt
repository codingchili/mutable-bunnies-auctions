package com.codingchili.bunneh.model

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Future

/**
 * Helper to ensure flowables/singles run on the correct scheduler to not
 * block the android main thread. Subscription runs async and the result
 * is observed on the main thread, which allows ui updates.
 */

fun <T> single(future: Future<T>): Single<T> {
    return Single.fromFuture(future)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> flow(future: Future<T>): Flowable<T> {
    return Flowable.fromFuture(future)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
}