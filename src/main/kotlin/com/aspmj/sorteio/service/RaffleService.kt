package com.aspmj.sorteio.service

import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBeginException
import com.aspmj.sorteio.extension.maxTime
import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.repository.RaffleRepository
import com.aspmj.sorteio.vo.RaffleParticipantVO
import com.aspmj.sorteio.vo.RaffleVO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RaffleService(
    val raffleRepository: RaffleRepository,
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

    @Transactional(propagation = Propagation.SUPPORTS)
    fun loadRaffles(): List<RaffleVO> {
        return raffleRepository.findAll().map {
            RaffleVO(it)
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    fun findRaffleVO(id: String): RaffleVO {
        return RaffleVO(raffleRepository.getOne(UUID.fromString(id)))
    }

    @Transactional
    fun delete(id: String) {
        raffleRepository.deleteById(UUID.fromString(id));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    fun loadAllRaffled(raffleId: String): MutableList<RaffleParticipantVO> {
        val raffle = raffleRepository.getOne(UUID.fromString(raffleId))
        return raffleParticipantRepository.findByRaffleAndRaffledDateIsNotNull(raffle).map { RaffleParticipantVO(it) }
            .toMutableList()
    }

    fun validateRaffle(id: String) {
        val raffle = raffleRepository.getOne(UUID.fromString(id))

        val today = Date()

        if (today.before(raffle.beginDate)) {
            throw DateLimitStillNotBeginException()
        }

        if (today.after(raffle.endDate) || today.after(raffle.raffleDate)) {
            throw DateLimitExceedException()
        }
    }
}