package com.aspmj.sorteio.service

import com.aspmj.sorteio.config.broker.BrokerConstants
import com.aspmj.sorteio.config.broker.BrokerMessage
import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBeginException
import com.aspmj.sorteio.exception.NoParticipantsException
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.exception.ParticipantAlreadyRaffledException
import com.aspmj.sorteio.exception.RaffleException
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
import kotlin.random.Random

@Service
class RaffleParticipantService(
    private val raffleParticipantRepository: RaffleParticipantRepository,
    private val raffleRepository: RaffleRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val participantExchange: DirectExchange,
    private val raffleService: RaffleService
) {

    @Transactional(propagation = Propagation.SUPPORTS)
    @Throws(ParticipantAlreadyRaffledException::class)
    fun checkParticipantAlreadyRaffled(participant: RaffleParticipant, raffleId: UUID) {
        if (raffleParticipantRepository.existsParticipantAlreadyRaffled(participant.id!!, raffleId)) {
            throw ParticipantAlreadyRaffledException(participant.name)
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    fun existsByPhoneAndRaffle(phone: String, raffle: Raffle): Boolean {
        return raffleParticipantRepository.existsByPhoneAndRaffle(phone, raffle)
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    fun findById(id: Long): RaffleParticipantVO = RaffleParticipantVO(raffleParticipantRepository.getOne(id))

    @Transactional(propagation = Propagation.SUPPORTS)
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

        raffleService.validateRaffle(vo.raffleId!!)

        if (existsByPhoneAndRaffle(vo.phone, raffle))
            throw ParticipantAlreadyExistsException()

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

    @Transactional(propagation = Propagation.SUPPORTS)
    @kotlin.jvm.Throws(
        DateLimitStillNotBeginException::class,
        DateLimitExceedException::class,
        ParticipantAlreadyExistsException::class
    )
    fun sendParticipantToQueue(vo: RaffleParticipantVO): RaffleParticipantVO? {

        val response = rabbitTemplate.convertSendAndReceiveAsType(
            participantExchange.name,
            BrokerConstants.NEW_PARTICIPANT_ROUTE,
            BrokerMessage(vo),
            object : ParameterizedTypeReference<BrokerMessage<RaffleParticipantVO>>() {}
        ) ?: throw Exception("Houve uma falha ao adicionar o novo participante")

        return if (response.error != null) throw RaffleException(response.error) else response.payload
    }

    @Transactional
    @Throws(NoParticipantsException::class)
    fun raffleParticipant(raffleId: String): RaffleParticipantVO {
        val raffle = raffleRepository.getOne(UUID.fromString(raffleId))

        val index = if (raffle.participants.count() > 0) Random.nextInt(raffle.participants.count()) else throw NoParticipantsException()

        val participant = raffle.participants[index]

        checkParticipantAlreadyRaffled(participant, raffle.id!!)

        participant.raffledDate = Date()

        raffleParticipantRepository.save(participant)
        return RaffleParticipantVO(participant)
    }
}