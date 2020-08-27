package com.codingchili.bunneh.model

import java.util.concurrent.CompletableFuture

fun main(args: Array<String>) {
    System.out.println("MAIN THREAD = ${Thread.currentThread().id}")
    CompletableFuture.runAsync {
        System.out.println("ASYNC THREAD ${Thread.currentThread().id}")
        Thread.sleep(2000)
    }.thenApply {
        System.out.println("APPLY THREAD = ${Thread.currentThread().id}")
    }
    System.out.println("MAIN THREAD = ${Thread.currentThread().id}")
    Thread.sleep(2500)
}

class Authentication(
    var token: String,
    var message: String?
) {
    fun success(): Boolean {
        return message != null
    }
}