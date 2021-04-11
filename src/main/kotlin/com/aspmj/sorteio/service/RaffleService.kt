package com.aspmj.sorteio.service

import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBegin
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.model.RaffleParticipant
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.repository.RaffleRepository
import com.aspmj.sorteio.vo.RaffleParticipantVO
import com.aspmj.sorteio.vo.RaffleVO
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional
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
            beginDate = vo.beginDate!!.maxTime(),
            endDate = vo.endDate!!.maxTime(),
            raffleDate = vo.raffleDate!!.maxTime()
        )

        raffleRepository.save(raffle)
    }

    fun addParticipantToRaffle(vo: RaffleParticipantVO): RaffleParticipantVO {
        val raffle = raffleRepository.getOne(UUID.fromString(vo.raffleId!!))

        val today = Date()

        if (today.before(raffle.beginDate)) {
            throw DateLimitStillNotBegin()
        }

        if (today.after(raffle.endDate)) {
            throw DateLimitExceedException()
        }

        val participant =
            if (raffleParticipantService.existsByPhoneAndRaffle(
                    vo.phone,
                    raffle
                )
            ) throw ParticipantAlreadyExistsException() else {
                RaffleParticipant(
                    name = vo.name,
                    phone = vo.phone,
                    email = vo.email,
                    raffle = raffle
                )
            }

        raffleParticipantRepository.save(participant)

        vo.id = participant.id
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

    fun raffleParticipant(raffleId: String): RaffleParticipantVO {
        val raffle = raffleRepository.getOne(UUID.fromString(raffleId))
        val index = Random.nextInt(raffle.participants.count())

        val participant = raffle.participants[index]

        raffleParticipantService.checkParticipantAlreadyRaffled(participant.id!!, raffle.id.toString())

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

fun Date?.maxTime(): Date {
    val calendar = Calendar.getInstance()

    calendar.time = this ?: Date()
    calendar.set(Calendar.HOUR, calendar.getActualMaximum(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE))
    calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND))

    return calendar.time
}
