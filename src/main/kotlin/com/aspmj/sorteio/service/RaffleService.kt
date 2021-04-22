package com.aspmj.sorteio.service

import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBeginException
import com.aspmj.sorteio.exception.NoParticipantsException
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.extension.maxTime
import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.model.RaffleParticipant
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.repository.RaffleRepository
import com.aspmj.sorteio.vo.RaffleParticipantVO
import com.aspmj.sorteio.vo.RaffleVO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.Throws
import kotlin.random.Random

@Service
class RaffleService(
    val raffleRepository: RaffleRepository,
    val raffleParticipantService: RaffleParticipantService,
    val raffleParticipantRepository: RaffleParticipantRepository
) {

    @Transactional
    fun saveRaffle(vo: RaffleVO) {

        val raffle = Raffle(
            id = vo.id,
            title = vo.title,
            beginDate = vo.beginDate!!,
            endDate = vo.endDate!!.maxTime(),
            raffleDate = vo.raffleDate!!.maxTime()
        )

        raffleRepository.save(raffle)
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun loadRaffles(): List<RaffleVO> {
        return raffleRepository.findAll().map {
            RaffleVO(it)
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun findRaffle(id: String): RaffleVO {
        return RaffleVO(raffleRepository.getOne(UUID.fromString(id)))
    }

    @Transactional
    fun delete(id: String) {
        raffleRepository.deleteById(UUID.fromString(id));
    }

    @Transactional
    @Throws(NoParticipantsException::class)
    fun raffleParticipant(raffleId: String): RaffleParticipantVO {
        val raffle = raffleRepository.getOne(UUID.fromString(raffleId))

        val index = if (raffle.participants.count() > 0) Random.nextInt(raffle.participants.count()) else throw NoParticipantsException()

        val participant = raffle.participants[index]

        raffleParticipantService.checkParticipantAlreadyRaffled(participant, raffle.id!!)

        participant.raffledDate = Date()

        raffleParticipantRepository.save(participant)
        return RaffleParticipantVO(participant)
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun loadAllRaffled(raffleId: String): MutableList<RaffleParticipantVO> {
        val raffle = raffleRepository.getOne(UUID.fromString(raffleId))
        return raffleParticipantRepository.findByRaffleAndRaffledDateIsNotNull(raffle).map { RaffleParticipantVO(it) }
            .toMutableList()
    }
}