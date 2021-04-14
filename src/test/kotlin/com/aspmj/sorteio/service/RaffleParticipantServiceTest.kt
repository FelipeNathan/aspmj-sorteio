package com.aspmj.sorteio.service

import com.aspmj.sorteio.exception.ParticipantAlreadyRaffledException
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
class RaffleParticipantServiceTest {

    @Mock
    lateinit var repository: RaffleParticipantRepository

    @InjectMocks
    lateinit var service: RaffleParticipantService

    @Test
    fun `when already raffled then throw ParticipantAlreadyRaffledException`() {
        val uuid = UUID.randomUUID()
        val participant = newParticipant(newRaffle())

        `when`(repository.existsParticipantAlreadyRaffled(participant.id!!, uuid)).thenReturn(true)

        val throwable = assertThrows<ParticipantAlreadyRaffledException> { service.checkParticipantAlreadyRaffled(participant, uuid) }

        assertNotNull(throwable)
        assertEquals("Participante ${participant.name} foi sorteado novamente", throwable.message)
    }

    @Test
    fun `when not raffled then do nothing`() {
        val uuid = UUID.randomUUID()
        val participant = newParticipant(newRaffle())

        `when`(repository.existsParticipantAlreadyRaffled(participant.id!!, uuid)).thenReturn(false)

        assertDoesNotThrow { service.checkParticipantAlreadyRaffled(participant, uuid) }
    }

    @Test
    fun `when findById then return VO Object mirror`() {
        val participant = newParticipant(newRaffle())

        `when`(repository.getOne(participant.id!!)).thenReturn(participant)

        val vo = service.findById(participant.id!!)

        assertEquals(vo.id, participant.id)
        assertEquals(vo.name, participant.name)
        assertEquals(vo.phone, participant.phone)
        assertEquals(vo.email, participant.email)
        assertEquals(vo.raffleId, participant.raffle.id.toString())
        assertEquals(vo.raffledDate, participant.raffledDate)
    }
}