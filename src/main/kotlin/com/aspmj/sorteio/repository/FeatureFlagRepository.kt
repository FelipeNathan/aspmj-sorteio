package com.aspmj.sorteio.repository

import com.aspmj.sorteio.model.FeatureFlag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeatureFlagRepository : JpaRepository<FeatureFlag, FeatureFlag.FLAGS>