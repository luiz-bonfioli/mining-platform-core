package com.mining.platform.core.communication.transaction.inbound

import com.mining.platform.core.datasource.AbstractRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * The Inbound repository
 *
 * @author luiz.bonfioli
 */
@Repository
interface InboundRepository : AbstractRepository<InboundEntity, UUID>
