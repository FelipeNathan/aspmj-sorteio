package com.aspmj.sorteio.service

import org.springframework.stereotype.Service
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.core.DirectExchange
import org.springframework.core.ParameterizedTypeReference
import com.aspmj.sorteio.config.broker.BrokerClientConfig
import com.aspmj.sorteio.vo.RaffleParticipantVO
import java.lang.reflect.ParameterizedType

@Service
class BrokerService(
    private val directExchange: DirectExchange,
    private val rabbitTemplate: RabbitTemplate
) {

    fun <T> sendCreation(vo: RaffleParticipantVO): T {

        return rabbitTemplate.convertSendAndReceiveAsType(
            directExchange.getName(), 
            ROUTING_KEY, 
            vo, 
            object : ParameterizedTypeReference<T>(){}
            )

    }

    companion object {
        const val ROUTING_KEY: String = "new_participant"
    }
}