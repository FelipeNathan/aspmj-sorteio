package com.aspmj.sorteio.config

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AppUserDetail(
    private val username: String,
    private val password: String,
    private val authority: MutableList<out GrantedAuthority> = mutableListOf()
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authority

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}