package com.mining.platform.core.infrastructure.changelog

import com.mining.platform.core.datasource.AbstractRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * The ChangeLog repository
 *
 * @author luiz.bonfioli
 */
@Repository
interface ChangeLogRepository : AbstractRepository<ChangeLogEntity, UUID> {

    fun existsByCompanyToken(companyToken: String): Boolean

}
