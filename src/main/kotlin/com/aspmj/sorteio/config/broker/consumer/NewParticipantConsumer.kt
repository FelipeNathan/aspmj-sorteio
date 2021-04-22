package com.aspmj.sorteio.config.broker.consumer

import com.aspmj.sorteio.config.broker.BrokerConstants
import com.aspmj.sorteio.config.broker.BrokerMessage
import com.aspmj.sorteio.exception.RaffleException
import com.aspmj.sorteio.service.RaffleParticipantService
import com.aspmj.sorteio.vo.RaffleParticipantVO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class NewParticipantConsumer(
    private val raffleParticipantService: RaffleParticipantService
) {

    @RabbitListener(queues = [BrokerConstants.NEW_PARTICIPANT_ROUTE])
    fun receiver(participant: BrokerMessage<RaffleParticipantVO>): BrokerMessage<RaffleParticipantVO> {
        try {
            LOG.info("New participant received: $participant")

            val newParticipant = participant.payload?.let {
                raffleParticipantService.addParticipantToRaffle(it)
            }

            return BrokerMessage(newParticipant)
        } catch (e: Exception) {

            if (e is RaffleException) {
                return BrokerMessage(error = e.message)
            }

            LOG.error("Error on add participant to raffle", e)
            throw e
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(NewParticipantConsumer::class.java)
    }
}