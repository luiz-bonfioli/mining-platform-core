package com.mining.platform.core.transaction.outbound

import com.mining.platform.core.datasource.AbstractRepository
import org.springframework.stereotype.Repository
import java.util.*


/**
 * The Outbound repository
 *
 * @author luiz.bonfioli
 */
@Repository
interface OutboundRepository : AbstractRepository<OutboundEntity, UUID> {

    fun findTopByTransactionIdOrderByPackageNumberAsc(transactionId: UUID): OutboundEntity

    fun deleteByTransactionIdAndPackageNumber(transactionId: UUID, packageNumber: Int)

    fun deleteAllByTransactionId(transactionId: UUID)

    fun existsByTransactionId(transactionId: UUID?): Boolean
}
