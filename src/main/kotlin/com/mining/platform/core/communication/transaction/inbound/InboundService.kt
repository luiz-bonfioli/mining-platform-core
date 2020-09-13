package com.mining.platform.core.communication.transaction.inbound

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


/**
 * The Inbound service
 *
 * @author luiz.bonfioli
 */
@Service
class InboundService {

    @Autowired
    private lateinit var repository: InboundRepository

    fun save(entity: InboundEntity): InboundEntity = repository.save(entity)

    fun readNextAvailableFragment(transactionId: UUID?): InboundEntity? =
            repository.findTopByTransactionIdAndInboundStatusOrderByPackageNumberAsc(transactionId, InboundStatus.AVAIABLE)

    fun deleteById(id: UUID) = repository.deleteById(id)

    fun deleteAllByTransactionId(transactionId: UUID) = repository.deleteAllByTransactionId(transactionId)

    fun existsByTransactionId(transactionId: UUID?): Boolean = repository.existsByTransactionId(transactionId)

    fun setFragmentError(inbound: InboundEntity) = inbound.apply {
        inboundStatus = InboundStatus.ERROR
        repository.save(this)
    }
}
