package com.aspmj.sorteio.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class FeatureFlag(

    @field:Id
    @field:Enumerated(EnumType.STRING)
    val id: FLAGS,

    @field:Column
    var active: Boolean = false
) {

    enum class FLAGS(private var description: String) {
        CREATE_RAFFLE("Rotina para criar novos sorteios");
    }
}