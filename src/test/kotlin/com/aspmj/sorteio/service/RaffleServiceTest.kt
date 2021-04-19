package com.aspmj.sorteio.service

import com.aspmj.sorteio.MockitoHelper
import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBeginException
import com.aspmj.sorteio.exception.NoParticipantsException
import com.aspmj.sorteio.exception.ParticipantAlreadyExistsException
import com.aspmj.sorteio.exception.ParticipantAlreadyRaffledException
import com.aspmj.sorteio.extension.maxTime
import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.model.RaffleParticipant
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.repository.RaffleRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
class RaffleServiceTest {

    @Mock
    lateinit var raffleRepository: RaffleRepository

    @Mock
    lateinit var raffleParticipantService: RaffleParticipantService

    @Mock
    lateinit var raffleParticipantRepository: RaffleParticipantRepository

    @InjectMocks
    lateinit var raffleService: RaffleService

    @Test
    fun `when pass RaffleVO to Save then save Raffle`() {

        val vo = newRaffleVO()
        val argumentCaptor = ArgumentCaptor.forClass(Raffle::class.java)

        raffleService.saveRaffle(vo)

        verify(raffleRepository).save(argumentCaptor.capture())

        val raffle = argumentCaptor.value
        assertEquals(raffle.id, vo.id)
        assertEquals(raffle.title, vo.title)
        assertEquals(raffle.beginDate, vo.beginDate)
        assertEquals(raffle.endDate, vo.endDate.maxTime())
        assertEquals(raffle.raffleDate, vo.raffleDate.maxTime())
    }

    @Test
    fun `when add participant before of begin date then throw DateLimitStillNotBeginException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = tomorrow, endDate = tomorrow, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitStillNotBeginException> { raffleService.addParticipantToRaffle(participantVO) }

        verify(raffleParticipantService, never()).existsByPhoneAndRaffle(anyString(), MockitoHelper.anyObject())
        verify(raffleParticipantRepository, never()).save(MockitoHelper.anyObject())
    }

    @Test
    fun `when add participant after of end date then throw DateLimitExceedException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = yesterday, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitExceedException> { raffleService.addParticipantToRaffle(participantVO) }

        verify(raffleParticipantService, never()).existsByPhoneAndRaffle(anyString(), MockitoHelper.anyObject())
        verify(raffleParticipantRepository, never()).save(MockitoHelper.anyObject())
    }

    @Test
    fun `when add participant after of raffle date then throw DateLimitExceedException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = tomorrow, raffleDate = yesterday)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitExceedException> { raffleService.addParticipantToRaffle(participantVO) }

        verify(raffleParticipantService, never()).existsByPhoneAndRaffle(anyString(), MockitoHelper.anyObject())
        verify(raffleParticipantRepository, never()).save(MockitoHelper.anyObject())
    }

    @Test
    fun `when add participant that already submitted then throw ParticipantAlreadyExistsException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = tomorrow, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)
        `when`(raffleParticipantService.existsByPhoneAndRaffle(participantVO.phone, raffle)).thenReturn(true)

        assertThrows<ParticipantAlreadyExistsException> { raffleService.addParticipantToRaffle(participantVO) }

        verify(raffleParticipantRepository, never()).save(MockitoHelper.anyObject())
    }

    @Test
    fun `when add participant then new Id and Number plus one`() {

        val argumentCaptor = ArgumentCaptor.forClass(RaffleParticipant::class.java)
        val raffle = newRaffle(beginDate = yesterday, endDate = tomorrow, raffleDate = tomorrow)
        val participantVO = newParticipantVO(raffleId = raffle.id.toString())

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)
        `when`(raffleParticipantService.existsByPhoneAndRaffle(participantVO.phone, raffle)).thenReturn(false)
        `when`(raffleParticipantService.getLastNumberByRaffle(raffle)).thenReturn(5)

        raffleService.addParticipantToRaffle(participantVO)

        verify(raffleParticipantRepository).save(argumentCaptor.capture())
        val participant = argumentCaptor.value
        assertEquals(participant.name, participantVO.name)
        assertEquals(participant.phone, participantVO.phone)
        assertEquals(participant.email, participantVO.email)
        assertEquals(participant.raffle.id.toString(), participantVO.raffleId)
        assertEquals(participant.number, 6)
    }

    @Test
    fun `when have no participants then throw NoParticipantsException`() {

        val raffle = newRaffle()
        `when`(raffleRepository.getOne(MockitoHelper.anyObject())).thenReturn(raffle)

        assertThrows<NoParticipantsException> { raffleService.raffleParticipant(UUID.randomUUID().toString()) }

        verify(raffleParticipantService, never()).checkParticipantAlreadyRaffled(MockitoHelper.anyObject(), MockitoHelper.anyObject())
        verify(raffleParticipantRepository, never()).save(MockitoHelper.anyObject())
    }

    @Test
    fun `when participant already raffled then throw ParticipantAlreadyRaffledException`() {

        val raffle = newRaffle()
        raffle.participants.add(newParticipant(raffle))

        `when`(raffleRepository.getOne(MockitoHelper.anyObject())).thenReturn(raffle)
        `when`(raffleParticipantService.checkParticipantAlreadyRaffled(MockitoHelper.anyObject(), MockitoHelper.anyObject())).thenThrow(ParticipantAlreadyRaffledException("Jack Sparrow"))

        assertThrows<ParticipantAlreadyRaffledException> { raffleService.raffleParticipant(UUID.randomUUID().toString()) }

        verify(raffleParticipantRepository, never()).save(MockitoHelper.anyObject())
    }

    @Test
    fun `when participant not raffled then return ParticipantVO with raffledDate`() {

        val raffle = newRaffle()
        val participant = newParticipant(raffle)
        raffle.participants.add(participant)

        `when`(raffleRepository.getOne(MockitoHelper.anyObject())).thenReturn(raffle)
        doNothing().`when`(raffleParticipantService).checkParticipantAlreadyRaffled(MockitoHelper.anyObject(), MockitoHelper.anyObject())

        val vo = raffleService.raffleParticipant(UUID.randomUUID().toString())

        verify(raffleParticipantRepository).save(participant)
        assertEquals(vo.id, participant.id)
        assertEquals(vo.name, participant.name)
        assertEquals(vo.phone, participant.phone)
        assertEquals(vo.email, participant.email)
        assertEquals(vo.raffleId, participant.raffle.id.toString())
        assertEquals(vo.raffledDate, participant.raffledDate)
        assertNotNull(vo.raffledDate)
        assertNotNull(participant.raffledDate)
    }
}