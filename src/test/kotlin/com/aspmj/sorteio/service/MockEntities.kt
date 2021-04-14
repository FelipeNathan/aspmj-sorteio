package com.aspmj.sorteio.service

import com.aspmj.sorteio.extension.maxTime
import com.aspmj.sorteio.model.Raffle
import com.aspmj.sorteio.model.RaffleParticipant
import com.aspmj.sorteio.vo.RaffleParticipantVO
import com.aspmj.sorteio.vo.RaffleVO
import java.util.*

fun newParticipant(raffle: Raffle) = RaffleParticipant(1, "Felipe", "email@email.com", "(47) 99999-9999", raffle)
fun newRaffle(beginDate: Date = Date(), endDate: Date = Date(), raffleDate: Date = Date()) = Raffle(UUID.randomUUID(), "Sorteiasso", beginDate.maxTime(), endDate.maxTime(), raffleDate.maxTime())

fun newParticipantVO(date: Date? = null, raffleId: String = UUID.randomUUID().toString()) = RaffleParticipantVO(2, "Nathan", "email@liame.moc", "(99) 47474-4747", raffleId, date)
fun newRaffleVO(beginDate: Date = Date(), endDate: Date = Date(), raffleDate: Date = Date()) = RaffleVO(UUID.randomUUID(), "Sorteiasso", beginDate, endDate, raffleDate)

val yesterday: Date = Calendar.getInstance().apply {
    this.add(Calendar.DAY_OF_MONTH, -1)
}.time

val tomorrow: Date = Calendar.getInstance().apply {
    this.add(Calendar.DAY_OF_MONTH, 1)
}.time