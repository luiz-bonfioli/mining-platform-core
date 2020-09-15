package com.mining.platform.core.transaction

import com.mining.platform.core.datasource.AbstractRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * The Transaction repository
 *
 * @author luiz.bonfioli
 */
@Repository
interface TransactionRepository : AbstractRepository<TransactionEntity, UUID>
