package com.mining.platform.core.communication.transaction

import com.mining.platform.core.service.AbstractService
import com.mining.platform.core.service.DataService
import org.springframework.stereotype.Service

/**
 * The Transaction service
 *
 * @author luiz.bonfioli
 */
@Service
class TransactionService : AbstractService<TransactionEntity, TransactionRepository>(), DataService<TransactionEntity>
