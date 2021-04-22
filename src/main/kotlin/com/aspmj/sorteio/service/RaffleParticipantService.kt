package com.aspmj.sorteio.service

import com.aspmj.sorteio.config.broker.BrokerConstants
import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBeginException
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.exception.ParticipantAlreadyRaffledException
import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.model.RaffleParticipant
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.repository.RaffleRepository
import com.aspmj.sorteio.vo.RaffleParticipantVO
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RaffleParticipantService(
    private val raffleParticipantRepository: RaffleParticipantRepository,
    private val raffleRepository: RaffleRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val directExchange: DirectExchange
) {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Throws(ParticipantAlreadyRaffledException::class)
    fun checkParticipantAlreadyRaffled(participant: RaffleParticipant, raffleId: UUID) {
        if (raffleParticipantRepository.existsParticipantAlreadyRaffled(participant.id!!, raffleId)) {
            throw ParticipantAlreadyRaffledException(participant.name)
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun existsByPhoneAndRaffle(phone: String, raffle: Raffle): Boolean {
        return raffleParticipantRepository.existsByPhoneAndRaffle(phone, raffle)
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun findById(id: Long): RaffleParticipantVO = RaffleParticipantVO(raffleParticipantRepository.getOne(id))

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun getLastNumberByRaffle(raffle: Raffle) =
        raffleParticipantRepository.findLastNumberByRaffle(raffle.id!!.toString())

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @kotlin.jvm.Throws(
        DateLimitStillNotBeginException::class,
        DateLimitExceedException::class,
        ParticipantAlreadyExistsException::class
    )
    fun addParticipantToRaffle(vo: RaffleParticipantVO): RaffleParticipantVO {
        val raffle = raffleRepository.getOne(UUID.fromString(vo.raffleId!!))
        val lastNumber = getLastNumberByRaffle(raffle)

        val participant =
            RaffleParticipant(
                name = vo.name,
                phone = vo.phone,
                email = vo.email,
                raffle = raffle,
                number = (lastNumber ?: 0) + 1
            )

        raffleParticipantRepository.save(participant)

        vo.id = participant.id
        vo.number = participant.number
        return vo
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @kotlin.jvm.Throws(
        DateLimitStillNotBeginException::class,
        DateLimitExceedException::class,
        ParticipantAlreadyExistsException::class
    )
    fun sendParticipantToQueue(vo: RaffleParticipantVO): RaffleParticipantVO {

        val raffle = raffleRepository.getOne(UUID.fromString(vo.raffleId!!))

        val today = Date()

        if (today.before(raffle.beginDate)) {
            throw DateLimitStillNotBeginException()
        }

        if (today.after(raffle.endDate) || today.after(raffle.raffleDate)) {
            throw DateLimitExceedException()
        }

        if (existsByPhoneAndRaffle(vo.phone, raffle))
            throw ParticipantAlreadyExistsException()


        return rabbitTemplate.convertSendAndReceiveAsType(
            directExchange.name,
            BrokerConstants.NEW_PARTICIPANT_ROUTE,
            vo,
            object : ParameterizedTypeReference<RaffleParticipantVO>() {}
        ) ?: throw Exception("Houve uma falha ao salvar o participante")
    }
}