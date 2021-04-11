package com.aspmj.sorteio.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator

@Entity
data class User(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO, generator = "participant_generator")
    @field:SequenceGenerator(name = "participant_generator", sequenceName = "participant_sequence")
    val id: Long? = null,

    @field:Column
    val name: String,

    @field:Column
    val username: String,

    @field:Column
    val password: String
)