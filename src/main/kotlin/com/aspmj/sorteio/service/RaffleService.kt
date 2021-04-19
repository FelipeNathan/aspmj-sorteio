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
import java.util.*
import javax.transaction.Transactional
import kotlin.jvm.Throws
import kotlin.random.Random

@Service
@Transactional
class RaffleService(
    val raffleRepository: RaffleRepository,
    val raffleParticipantService: RaffleParticipantService,
    val raffleParticipantRepository: RaffleParticipantRepository
) {

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

    @Throws(DateLimitStillNotBeginException::class, DateLimitExceedException::class, ParticipantAlreadyExistsException::class)
    fun addParticipantToRaffle(vo: RaffleParticipantVO): RaffleParticipantVO {
        val raffle = raffleRepository.getOne(UUID.fromString(vo.raffleId!!))

        val today = Date()

        if (today.before(raffle.beginDate)) {
            throw DateLimitStillNotBeginException()
        }

        if (today.after(raffle.endDate) || today.after(raffle.raffleDate)) {
            throw DateLimitExceedException()
        }

        val participant =
            if (raffleParticipantService.existsByPhoneAndRaffle(
                    vo.phone,
                    raffle
                )
            ) throw ParticipantAlreadyExistsException() else {
                val lastNumber = raffleParticipantService.getLastNumberByRaffle(raffle)
                RaffleParticipant(
                    name = vo.name,
                    phone = vo.phone,
                    email = vo.email,
                    raffle = raffle,
                    number = (lastNumber ?: 0) + 1
                )
            }

        raffleParticipantRepository.save(participant)

        vo.id = participant.id
        vo.number = participant.number
        return vo
    }

    fun loadRaffles(): List<RaffleVO> {
        return raffleRepository.findAll().map {
            RaffleVO(it)
        }
    }

    fun findRaffle(id: String): RaffleVO {
        return RaffleVO(raffleRepository.getOne(UUID.fromString(id)))
    }

    fun delete(id: String) {
        raffleRepository.deleteById(UUID.fromString(id));
    }

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

    fun loadAllRaffled(raffleId: String): MutableList<RaffleParticipantVO> {
        val raffle = raffleRepository.getOne(UUID.fromString(raffleId))
        return raffleParticipantRepository.findByRaffleAndRaffledDateIsNotNull(raffle).map { RaffleParticipantVO(it) }
            .toMutableList()
    }
}