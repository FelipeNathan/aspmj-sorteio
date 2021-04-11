package com.aspmj.sorteio.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator

@Entity
data class RaffleParticipant(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO, generator = "participant_generator")
    @field:SequenceGenerator(name = "participant_generator", sequenceName = "participant_sequence")
    val id: Long? = null,

    @field:Column
    val name: String,

    @field:Column
    val email: String,

    @field:Column
    val phone: String,

    @field:ManyToOne
    @field:JoinColumn(name = "raffle_id")
    val raffle: Raffle,

    @field:Column
    var raffledDate: Date? = null
)