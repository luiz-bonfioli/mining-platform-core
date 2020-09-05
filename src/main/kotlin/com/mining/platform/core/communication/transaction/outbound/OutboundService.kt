package com.mining.platform.core.communication.transaction.outbound

import com.mining.platform.core.service.AbstractService
import com.mining.platform.core.service.DataService
import org.springframework.stereotype.Service

/**
 * The Outbound service
 *
 * @author luiz.bonfioli
 */
@Service
class OutboundService : AbstractService<OutboundEntity, OutboundRepository>(), DataService<OutboundEntity>
