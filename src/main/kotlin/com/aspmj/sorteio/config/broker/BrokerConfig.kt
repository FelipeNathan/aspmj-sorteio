package com.aspmj.sorteio.config.broker

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BrokerConfig(
    @Value("\${raffle.rabbitmq.url}")
    private val rabbitUrl: String,

    @Value("\${raffle.rabbitmq.timeout}")
    private val timeout: Long
) {

    @Bean
    fun messageConverter(): MessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplate(): RabbitTemplate {

        return RabbitTemplate(connectionFactory()).apply {
            this.messageConverter = messageConverter()
            this.setReceiveTimeout(timeout)
            this.setReplyTimeout(timeout)
        }
    }

    @Bean
    fun connectionFactory(): CachingConnectionFactory {
        return CachingConnectionFactory().apply {
            setUri(rabbitUrl)
            setConnectionTimeout(timeout.toInt())
        }
    }
}