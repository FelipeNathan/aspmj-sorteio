package com.aspmj.sorteio.repository

import com.aspmj.sorteio.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
}