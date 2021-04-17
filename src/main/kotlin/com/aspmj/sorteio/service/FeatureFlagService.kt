package com.aspmj.sorteio.service

import com.aspmj.sorteio.config.ROLES
import com.aspmj.sorteio.model.FeatureFlag
import com.aspmj.sorteio.repository.FeatureFlagRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class FeatureFlagService(
    private val repository: FeatureFlagRepository
) {

    fun findById(id: FeatureFlag.FLAGS): FeatureFlag? = repository.getOne(id)

    fun checkPermission(id: FeatureFlag.FLAGS): Boolean {
        val isAdmin =
            SecurityContextHolder.getContext().authentication.authorities.contains(SimpleGrantedAuthority(ROLES.ADMIN.role))
        val permission = findById(id)

        return permission?.active ?: false || isAdmin
    }
}