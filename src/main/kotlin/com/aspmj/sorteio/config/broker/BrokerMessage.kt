package com.aspmj.sorteio.config.broker

import java.io.Serializable

data class BrokerMessage<T>(
    val payload: T? = null,
    val error: String? = null
) : Serializable