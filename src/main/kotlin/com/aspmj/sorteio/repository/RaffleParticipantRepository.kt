package com.aspmj.sorteio.repository

import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.model.RaffleParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RaffleParticipantRepository : JpaRepository<RaffleParticipant, UUID> {

    fun existsByPhoneAndRaffle(phone: String, raffle: Raffle): Boolean

    fun findByRaffleAndRaffledDateIsNotNull(raffle: Raffle): MutableList<RaffleParticipant>

    @Query(
        value = "SELECT CASE WHEN (count(*) > 0) THEN true ELSE false END FROM raffle_participant rp WHERE rp.id = :participantId AND rp.raffle_id = :raffleId AND rp.raffled_date IS NOT NULL",
        nativeQuery = true
    )
    fun existsParticipantAlreadyRaffled(
        @Param("participantId") raffleParticipantId: Long,
        @Param("raffleId") raffleId: String
    ): Boolean
}