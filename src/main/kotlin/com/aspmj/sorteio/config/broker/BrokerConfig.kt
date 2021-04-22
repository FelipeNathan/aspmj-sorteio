package com.aspmj.sorteio.config.broker

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
class BrokerConfig(
    @Value("\${rabbitmq.url}")
    private val rabbitUrl: String,
) {

    @Bean
    fun directExchange() = DirectExchange(BrokerConstants.RAFFLE_EXCHANGE)

    @Bean
    fun queue() = Queue(BrokerConstants.NEW_PARTICIPANT_ROUTE)

    @Bean
    fun binding(directExchange: DirectExchange, queue: Queue): Binding {
        return BindingBuilder.bind(queue).to(directExchange).with(BrokerConstants.NEW_PARTICIPANT_ROUTE)
    }

    @Bean
    fun rabbitTemplate(): RabbitTemplate {

        val retryTemplate = RetryTemplate().apply {
            setBackOffPolicy(ExponentialBackOffPolicy())
        }

        return RabbitTemplate(connectionFactory()).apply {
            setRetryTemplate(retryTemplate)
        }
    }

    @Bean
    fun rabbitAdmin(): RabbitAdmin = RabbitAdmin(connectionFactory())

    @Bean
    fun connectionFactory(): CachingConnectionFactory {
        return CachingConnectionFactory().apply {
            setUri(rabbitUrl)
        }
    }
}