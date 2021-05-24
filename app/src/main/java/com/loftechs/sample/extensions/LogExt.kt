package com.loftechs.sample.extensions

import timber.log.Timber

fun Any.logThread(message: String) {
    Timber.tag("${this::class.java.simpleName}_thread").d("[${Thread.currentThread().name}] $message")
}

fun Any.logDebug(message: String) {
    Timber.tag(this::class.java.simpleName).d(message)
}

fun Any.logError(functionName: String, throwable: Throwable) {
    Timber.tag(this::class.java.simpleName).e("$functionName error: $throwable")
}