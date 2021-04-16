package com.aspmj.sorteio.service

import com.aspmj.sorteio.config.AppUserDetail
import com.aspmj.sorteio.config.ROLES
import com.aspmj.sorteio.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Repository

@Repository
class AppUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        return userRepository.findByUsername(username!!)?.let {

            val roles = mutableListOf(SimpleGrantedAuthority(ROLES.USER.role))

            if (username == "raffle_admin")
                roles.add(SimpleGrantedAuthority(ROLES.ADMIN.role))

            AppUserDetail(it.username, it.password, roles)
        }
    }
}