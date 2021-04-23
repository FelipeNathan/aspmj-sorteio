package com.aspmj.sorteio.service

import com.aspmj.sorteio.MockitoHelper
import com.aspmj.sorteio.config.broker.BrokerConstants
import com.aspmj.sorteio.config.broker.BrokerMessage
import com.aspmj.sorteio.exception.NoParticipantsException
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.exception.ParticipantAlreadyRaffledException
import com.aspmj.sorteio.exception.RaffleException
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.repository.RaffleRepository
import com.aspmj.sorteio.vo.RaffleParticipantVO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.anyLong
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
    lateinit var participantExchange: DirectExchange

    @Mock
    lateinit var raffleService: RaffleService

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
    fun `when have no participants then throw NoParticipantsException`() {

        val raffle = newRaffle()
        `when`(raffleRepository.getOne(MockitoHelper.anyObject())).thenReturn(raffle)

        assertThrows<NoParticipantsException> { service.raffleParticipant(UUID.randomUUID().toString()) }

        Mockito.verify(service, Mockito.never()).checkParticipantAlreadyRaffled(MockitoHelper.anyObject(), MockitoHelper.anyObject())
        Mockito.verify(repository, Mockito.never()).save(MockitoHelper.anyObject())
    }

    @Test
    fun `when participant already raffled then throw ParticipantAlreadyRaffledException`() {

        val raffle = newRaffle()
        raffle.participants.add(newParticipant(raffle))

        `when`(raffleRepository.getOne(MockitoHelper.anyObject())).thenReturn(raffle)
        `when`(repository.existsParticipantAlreadyRaffled(anyLong(), MockitoHelper.anyObject())).thenReturn(true)

        assertThrows<ParticipantAlreadyRaffledException> { service.raffleParticipant(UUID.randomUUID().toString()) }

        Mockito.verify(repository, Mockito.never()).save(MockitoHelper.anyObject())
    }

    @Test
    fun `when participant not raffled then return ParticipantVO with raffledDate`() {

        val raffle = newRaffle()
        val participant = newParticipant(raffle)
        raffle.participants.add(participant)

        `when`(raffleRepository.getOne(MockitoHelper.anyObject())).thenReturn(raffle)
        Mockito.doNothing().`when`(service).checkParticipantAlreadyRaffled(MockitoHelper.anyObject(), MockitoHelper.anyObject())

        val vo = service.raffleParticipant(UUID.randomUUID().toString())

        Mockito.verify(repository).save(participant)
        assertEquals(vo.id, participant.id)
        assertEquals(vo.name, participant.name)
        assertEquals(vo.phone, participant.phone)
        assertEquals(vo.email, participant.email)
        assertEquals(vo.raffleId, participant.raffle.id.toString())
        assertEquals(vo.raffledDate, participant.raffledDate)
        assertNotNull(vo.raffledDate)
        assertNotNull(participant.raffledDate)
    }

    @Test
    fun `when send right vo to rabbitmq then should not throw any exception`() {

        val participant = newParticipantVO()
        val response = BrokerMessage(participant)

        `when`(rabbitTemplate.convertSendAndReceiveAsType(
            Mockito.eq(participantExchange.name),
            Mockito.eq(BrokerConstants.NEW_PARTICIPANT_ROUTE),
            MockitoHelper.anyObject(BrokerMessage::class.java),
            MockitoHelper.anyObject(ParameterizedTypeReference::class.java)
        )).thenReturn(response)

        assertDoesNotThrow { service.sendParticipantToQueue(participant) }
    }

    @Test
    fun `when rabbitmq return null then should not throw Exception`() {

        val participant = newParticipantVO()

        `when`(rabbitTemplate.convertSendAndReceiveAsType(
            Mockito.eq(participantExchange.name),
            Mockito.eq(BrokerConstants.NEW_PARTICIPANT_ROUTE),
            MockitoHelper.anyObject(BrokerMessage::class.java),
            MockitoHelper.anyObject(ParameterizedTypeReference::class.java)
        )).thenReturn(null)

        val exception = assertThrows<Exception> { service.sendParticipantToQueue(participant) }
        assertEquals("Houve uma falha ao adicionar o novo participante", exception.message)
    }

    @Test
    fun `when rabbitmq return response with error then should not throw RaffleException with error message`() {

        val participant = newParticipantVO()
        val thrown = ParticipantAlreadyExistsException()
        val response = BrokerMessage<RaffleParticipantVO>(error = thrown.message)

        `when`(rabbitTemplate.convertSendAndReceiveAsType(
            Mockito.eq(participantExchange.name),
            Mockito.eq(BrokerConstants.NEW_PARTICIPANT_ROUTE),
            MockitoHelper.anyObject(BrokerMessage::class.java),
            MockitoHelper.anyObject(ParameterizedTypeReference::class.java)
        )).thenReturn(response)

        val exception = assertThrows<RaffleException> { service.sendParticipantToQueue(participant) }
        assertEquals(thrown.message, exception.message)
    }
}