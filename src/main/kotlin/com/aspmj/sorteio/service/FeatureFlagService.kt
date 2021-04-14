package com.aspmj.sorteio.service

import com.aspmj.sorteio.model.FeatureFlag
import com.aspmj.sorteio.repository.FeatureFlagRepository
import org.springframework.stereotype.Service

@Service
class FeatureFlagService(
    private val repository: FeatureFlagRepository
) {

    fun findById(id: FeatureFlag.FLAGS): FeatureFlag? = repository.getOne(id)
}