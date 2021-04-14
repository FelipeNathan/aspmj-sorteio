package com.aspmj.sorteio

import org.mockito.Mockito

object MockitoHelper {

    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T> uninitialized(): T = null as T
}