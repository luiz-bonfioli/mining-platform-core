package com.mining.platform.core.communication.transaction.outbound

import com.mining.platform.core.communication.transaction.TransactionEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


/**
 * The Outbound service
 *
 * @author luiz.bonfioli
 */
@Service
class OutboundService {

    @Autowired
    private lateinit var repository: OutboundRepository

    fun findById(id: UUID): OutboundEntity? = repository.findById(id).orElse(null)

    fun addFragment(transaction: TransactionEntity, content: ByteArray) =
            repository.save(OutboundEntity(
                    content = content,
                    transaction = transaction,
                    packageNumber = transaction.numberOfPackages
            ))

    fun readNextFragment(transactionId: UUID): OutboundEntity =
            repository.findTopByTransactionIdOrderByPackageNumberAsc(transactionId)

    fun existsByTransactionId(transactionId: UUID?): Boolean = repository.existsByTransactionId(transactionId)

    fun deleteById(id: UUID) = repository.deleteById(id)

    //@Transactional
    fun deleteAllByTransactionId(transactionId: UUID) = repository.deleteAllByTransactionId(transactionId)


}
