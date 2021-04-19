package com.aspmj.sorteio.repository

import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.model.RaffleParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.lang.Nullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RaffleParticipantRepository : JpaRepository<RaffleParticipant, Long> {

    fun existsByPhoneAndRaffle(phone: String, raffle: Raffle): Boolean

    fun findByRaffleAndRaffledDateIsNotNull(raffle: Raffle): MutableList<RaffleParticipant>

    @Query(
        value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM RaffleParticipant rp WHERE rp.id = :participantId AND rp.raffle.id = :raffleId AND rp.raffledDate IS NOT NULL"
    )
    fun existsParticipantAlreadyRaffled(
        @Param("participantId") raffleParticipantId: Long,
        @Param("raffleId") raffleId: UUID
    ): Boolean

    @Nullable
    @Query("SELECT number FROM raffle_participant rp WHERE rp.raffle_id = :raffleId ORDER BY number DESC LIMIT 1", nativeQuery = true)
    fun findLastNumberByRaffle(raffleId: String): Int?
}