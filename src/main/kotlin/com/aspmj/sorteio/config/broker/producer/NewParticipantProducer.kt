package com.aspmj.sorteio.config.broker.producer

import com.aspmj.sorteio.config.broker.BrokerConstants
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class NewParticipantProducer {

    @Bean
    fun participantQueue(): Queue = QueueBuilder
        .durable(BrokerConstants.NEW_PARTICIPANT_ROUTE)
        .deadLetterExchange(participantDqlExchange().name)
        .deadLetterRoutingKey(participantDlqQueue().name)
        .build()

    @Bean
    fun participantExchange(): DirectExchange = DirectExchange(BrokerConstants.RAFFLE_EXCHANGE)

    @Bean
    fun participantDlqQueue(): Queue = Queue(BrokerConstants.NEW_PARTICIPANT_ROUTE_DLQ)

    @Bean
    fun participantDqlExchange(): DirectExchange = DirectExchange(BrokerConstants.RAFFLE_EXCHANGE_DLQ)

    @Bean
    fun binding(): Binding = BindingBuilder
        .bind(participantQueue())
        .to(participantExchange())
        .withQueueName()

    @Bean
    fun bindingDeadLetter(): Binding = BindingBuilder
        .bind(participantDlqQueue())
        .to(participantDqlExchange())
        .withQueueName()
}