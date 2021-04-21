package com.aspmj.sorteio.config.broker

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter

@Configuration
class BrokerClientConfig {

    @Bean
    fun directExchange() = DirectExchange(RAFFLE_EXCHANGE)

    
    @Bean
    fun jackson2MessageConverter() : MessageConverter = Jackson2JsonMessageConverter()

    companion object {
        const val RAFFLE_EXCHANGE = "raffle.exchange"
    }
}