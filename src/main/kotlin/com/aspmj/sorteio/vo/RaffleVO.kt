package com.aspmj.sorteio.vo

import com.aspmj.sorteio.model.Raffle
import org.springframework.format.annotation.DateTimeFormat
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RaffleVO(
    val id: UUID? = null,

    @field:NotBlank(message = "Título obrigatório")
    var title: String = "",

    @field:DateTimeFormat(pattern = "dd/MM/yyyy")
    @field:NotNull(message = "Data de início para cadastro de participantes obrigatório")
    var beginDate: Date? = null,

    @field:DateTimeFormat(pattern = "dd/MM/yyyy")
    @field:NotNull(message = "Data de início para cadastro de participantes obrigatório")
    var endDate: Date? = null,

    @field:DateTimeFormat(pattern = "dd/MM/yyyy")
    @field:NotNull(message = "Data do sorteio obrigatório")
    var raffleDate: Date? = null,

    var participants: MutableList<RaffleParticipantVO> = mutableListOf()

) {
    constructor(raffle: Raffle) : this(
        raffle.id,
        raffle.title,
        raffle.beginDate,
        raffle.endDate,
        raffle.raffleDate,
        raffle.participants.map { RaffleParticipantVO(it) }.toMutableList(),
    )

    val participantsCount: Int
        get() = participants.count()

    val canRaffle: Boolean
        get() = (Date().before(beginDate)) || Date().before(raffleDate)
}