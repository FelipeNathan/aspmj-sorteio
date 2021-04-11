package com.aspmj.sorteio.model

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Raffle(
    @field:Id
    @field:Type(type="uuid-char")
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @field:Column
    val title: String,

    @field:Column(name = "begin_date")
    val beginDate: Date,

    @field:Column(name = "end_date")
    val endDate: Date,

    @field:Column(name = "raffle_date")
    val raffleDate: Date,

    @field:OneToMany(mappedBy = "raffle", cascade = [CascadeType.REMOVE])
    val participants: MutableList<RaffleParticipant> = mutableListOf()
)