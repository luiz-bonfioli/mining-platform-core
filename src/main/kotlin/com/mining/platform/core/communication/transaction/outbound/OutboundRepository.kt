package com.mining.platform.core.communication.transaction.outbound

import com.mining.platform.core.datasource.AbstractRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * The Outbound repository
 *
 * @author luiz.bonfioli
 */
@Repository
interface OutboundRepository : AbstractRepository<OutboundEntity, UUID>
