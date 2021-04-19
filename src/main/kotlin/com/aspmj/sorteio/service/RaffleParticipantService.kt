package com.aspmj.sorteio.service

import com.aspmj.sorteio.exception.ParticipantAlreadyRaffledException
import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.model.RaffleParticipant
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.vo.RaffleParticipantVO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RaffleParticipantService(
        private val raffleParticipantRepository: RaffleParticipantRepository
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
    fun getLastNumberByRaffle(raffle: Raffle) = raffleParticipantRepository.findLastNumberByRaffle(raffle.id!!.toString())
}