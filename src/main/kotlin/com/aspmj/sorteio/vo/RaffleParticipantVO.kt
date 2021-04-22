package com.aspmj.sorteio.vo

import com.aspmj.sorteio.model.RaffleParticipant
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RaffleParticipantVO(
    @JsonProperty
    var id: Long? = null,

    @JsonProperty
    @field:NotBlank(message = "Nome é obrigatório")
    var name: String = "",

    @JsonProperty
    @field:NotBlank(message = "E-mail é obrigatório")
    var email: String = "",

    @JsonProperty
    @field:NotBlank(message = "Telefone é obrigatório")
    var phone: String = "",

    @JsonProperty
    @field:NotNull(message = "Sorteio obrigatório")
    var raffleId: String? = null,

    @JsonProperty
    var raffledDate: Date? = null,

    @JsonProperty
    var number: Int? = 0

) : Comparable<RaffleParticipantVO>, Serializable {
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