package com.aspmj.sorteio.service

import com.aspmj.sorteio.exception.DateLimitExceedException
import com.aspmj.sorteio.exception.DateLimitStillNotBeginException
import com.aspmj.sorteio.extension.maxTime
import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.repository.RaffleParticipantRepository
import com.aspmj.sorteio.repository.RaffleRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
class RaffleServiceTest {

    @Mock
    lateinit var raffleRepository: RaffleRepository

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
    fun `when validate raffle before of begin date then throw DateLimitStillNotBeginException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = tomorrow, endDate = tomorrow, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitStillNotBeginException> { raffleService.validateRaffle(participantVO.raffleId!!) }
    }

    @Test
    fun `when validate raffle after of end date then throw DateLimitExceedException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = yesterday, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitExceedException> { raffleService.validateRaffle(participantVO.raffleId!!) }
    }

    @Test
    fun `when validate raffle after of raffle date then throw DateLimitExceedException`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = tomorrow, raffleDate = yesterday)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertThrows<DateLimitExceedException> { raffleService.validateRaffle(participantVO.raffleId!!) }
    }

    @Test
    fun `when validate raffle in range of period then do nothing`() {

        val participantVO = newParticipantVO()
        val raffle = newRaffle(beginDate = yesterday, endDate = tomorrow, raffleDate = tomorrow)

        `when`(raffleRepository.getOne(UUID.fromString(participantVO.raffleId))).thenReturn(raffle)

        assertDoesNotThrow {  raffleService.validateRaffle(participantVO.raffleId!!) }
    }

}