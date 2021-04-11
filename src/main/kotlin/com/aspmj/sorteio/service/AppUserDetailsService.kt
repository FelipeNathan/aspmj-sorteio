package com.aspmj.sorteio.service

import com.aspmj.sorteio.config.AppUserDetail
import com.aspmj.sorteio.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Repository

@Repository
class AppUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        return userRepository.findByUsername(username!!)?.let {
            AppUserDetail(it.username, it.password, listOf(SimpleGrantedAuthority("ROLE_ADMIN")))
        }
    }
}