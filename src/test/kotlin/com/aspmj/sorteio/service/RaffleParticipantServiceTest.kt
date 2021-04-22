package com.aspmj.sorteio.service

import com.aspmj.sorteio.MockitoHelper
import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBeginException
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.exception.ParticipantAlreadyRaffledException
import com.aspmj.sorteio.model.RaffleParticipant
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.repository.RaffleRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Spy
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
class RaffleParticipantServiceTest {

    @Mock
    lateinit var repository: RaffleParticipantRepository

    @Mock
    lateinit var raffleRepository: RaffleRepository

    @Mock
    lateinit var rabbitTemplate: RabbitTemplate

    @Mock
    lateinit var directExchange: DirectExchange

    @Spy
    @InjectMocks
    lateinit var service: RaffleParticipantService

    @Test
    fun `when already raffled then throw ParticipantAlreadyRaffledException`() {
        val uuid = UUID.randomUUID()
        val participant = newParticipant(newRaffle())

        `when`(repository.existsParticipantAlreadyRaffled(participant.id!!, uuid)).thenReturn(true)

        val throwable = assertThrows<ParticipantAlreadyRaffledException> {
            service.checkParticipantAlreadyRaffled(
                participant,
                uuid
            )
        }

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
        assertEquals(vo.number, participant.number)
    }

    @Test
    fun `when add participant before of begin date then throw DateLimitStillNotBeginException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = tomorrow, endDate = tomorrow, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitStillNotBeginException> { service.sendParticipantToQueue(participantVO) }

        Mockito.verify(service, Mockito.never())
            .existsByPhoneAndRaffle(Mockito.anyString(), MockitoHelper.anyObject())
    }

    @Test
    fun `when add participant after of end date then throw DateLimitExceedException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = yesterday, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitExceedException> { service.sendParticipantToQueue(participantVO) }

        Mockito.verify(service, Mockito.never())
            .existsByPhoneAndRaffle(Mockito.anyString(), MockitoHelper.anyObject())
    }

    @Test
    fun `when add participant after of raffle date then throw DateLimitExceedException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = tomorrow, raffleDate = yesterday)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitExceedException> { service.sendParticipantToQueue(participantVO) }

        Mockito.verify(service, Mockito.never())
            .existsByPhoneAndRaffle(Mockito.anyString(), MockitoHelper.anyObject())
    }

    @Test
    fun `when add participant that already submitted then throw ParticipantAlreadyExistsException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = tomorrow, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)
        `when`(service.existsByPhoneAndRaffle(participantVO.phone, raffle)).thenReturn(true)

        assertThrows<ParticipantAlreadyExistsException> { service.sendParticipantToQueue(participantVO) }
    }
}