package com.aspmj.sorteio.repository

import com.aspmj.sorteio.model.Raffle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RaffleRepository : JpaRepository<Raffle, UUID>