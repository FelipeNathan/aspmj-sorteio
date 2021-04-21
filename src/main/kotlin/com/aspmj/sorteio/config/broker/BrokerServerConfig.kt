package com.aspmj.sorteio.config.broker

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Binding
import com.aspmj.sorteio.service.BrokerService

@Configuration
class BrokerServerConfig {

    @Bean
    fun directExchange() = DirectExchange(RAFFLE_EXCHANGE)

    @Bean
    fun queue() = Queue("request")

    @Bean
    fun binding(directExchange: DirectExchange, queue: Queue): Binding {
        return BindingBuilder.bind(queue).to(directExchange).with(BrokerService.ROUTING_KEY)
    }

    companion object {
        const val RAFFLE_EXCHANGE = "raffle.exchange"
    }
}