package com.aspmj.sorteio

import org.mockito.Mockito

object MockitoHelper {

    fun <T> anyObject(type: Class<T>? = null): T {
        if (type == null ) Mockito.any<T>() else Mockito.any<T>(type)
        return uninitialized()
    }

    fun <T> uninitialized(): T = null as T
}