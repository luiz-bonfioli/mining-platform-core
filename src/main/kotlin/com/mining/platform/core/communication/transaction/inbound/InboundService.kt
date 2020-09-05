package com.mining.platform.core.communication.transaction.inbound

import com.mining.platform.core.service.AbstractService
import com.mining.platform.core.service.DataService
import org.springframework.stereotype.Service

/**
 * The Inbound service
 *
 * @author luiz.bonfioli
 */
@Service
class InboundService : AbstractService<InboundEntity, InboundRepository>(), DataService<InboundEntity>
