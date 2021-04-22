package com.aspmj.sorteio.config.broker.consumer

import com.aspmj.sorteio.config.broker.BrokerConstants
import com.aspmj.sorteio.service.RaffleParticipantService
import com.aspmj.sorteio.vo.RaffleParticipantVO
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class BrokerConsumer(
    private val raffleParticipantService: RaffleParticipantService
) {

    @RabbitListener(queues = [BrokerConstants.NEW_PARTICIPANT_ROUTE], concurrency = "5")
    fun receiver(participant: RaffleParticipantVO): RaffleParticipantVO {
        return raffleParticipantService.addParticipantToRaffle(participant)
    }
}