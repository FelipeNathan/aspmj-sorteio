package com.aspmj.sorteio.vo

import com.aspmj.sorteio.model.RaffleParticipant
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RaffleParticipantVO(
    var id: Long? = null,

    @field:NotBlank(message = "Nome é obrigatório")
    var name: String = "",

    @field:NotBlank(message = "E-mail é obrigatório")
    var email: String = "",

    @field:NotBlank(message = "Telefone é obrigatório")
    var phone: String = "",

    @field:NotNull(message = "Sorteio obrigatório")
    var raffleId: String? = null,

    var raffledDate: Date? = null,

    var number: Int? = 0
) : Comparable<RaffleParticipantVO> {
    constructor(p: RaffleParticipant) : this(
        p.id,
        p.name,
        p.email,
        p.phone,
        p.raffle.id.toString(),
        p.raffledDate,
        p.number
    )

    override fun compareTo(other: RaffleParticipantVO): Int {
        val thisDate = this.raffledDate ?: Date()
        val otherDate = other.raffledDate ?: Date()

        return if (thisDate.before(otherDate)) -1 else 1
    }
}