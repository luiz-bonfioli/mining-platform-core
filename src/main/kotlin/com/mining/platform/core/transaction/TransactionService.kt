package com.mining.platform.core.transaction

import com.mining.platform.core.communication.CommunicationService
import com.mining.platform.core.communication.MessageListener
import com.mining.platform.core.communication.protocol.Protocol
import com.mining.platform.core.communication.protocol.Protocol.Topic
import com.mining.platform.core.transaction.export.ExportService
import com.mining.platform.core.transaction.import.ImportService
import com.mining.platform.core.transaction.inbound.InboundEntity
import com.mining.platform.core.transaction.inbound.InboundService
import com.mining.platform.core.transaction.outbound.OutboundService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The Transaction service
 *
 * @author luiz.bonfioli
 */
@Service
class TransactionService : MessageListener {

    @Autowired
    private lateinit var communicationService: CommunicationService

    @Autowired
    private lateinit var repository: TransactionRepository

    @Autowired
    private lateinit var outboundService: OutboundService

    @Autowired
    private lateinit var inboundService: InboundService

    @Autowired
    private lateinit var importService: ImportService

    @Autowired
    private lateinit var exportService: ExportService

    override fun onMessageArrived(eventId: Byte, payload: ByteArray, source: String) {
        var success = false
        when (eventId) {
            Protocol.Event.IMPORT -> success = importService.process(payload)
            Protocol.Event.EXPORT -> success = exportService.process(payload)
            Protocol.Event.TRANSACTION_STATUS -> success = processStatusRequest(payload, source)
            else -> logger.log(Level.SEVERE,
                    "Event not found, should be import or export instead of {0}.", eventId)
        }
        if (!success) {
            sendError(eventId, "Cannot proccess the event: $eventId", source)
        }
    }

    fun create(topic: String, transactionType: TransactionType): TransactionEntity =
            repository.save(TransactionEntity(
                    id = UUID.randomUUID(),
                    topic = topic,
                    transactionType = transactionType,
                    transactionStatus = TransactionStatus.OPEN))

    fun addFragment(transaction: TransactionEntity, content: ByteArray) {
        transaction.numberOfPackages += 1
        outboundService.addFragment(transaction, content)
    }

    fun execute(transaction: TransactionEntity) =
            transaction.id?.let { transactionId ->
                if (isTransactionEmpty(transaction)) {
                    sendEmptyTransaction(transaction.topic)
                    closeOutboundTransaction(transactionId)
                } else {
                    transaction.transactionStatus = TransactionStatus.AVAILABLE
                    repository.save(transaction)
                    sendFragmentsAvailable(transaction)
                }
            }

    fun isTransactionEmpty(transaction: TransactionEntity): Boolean =
            when (transaction.transactionType) {
                TransactionType.OUTBOUND -> !outboundService.existsByTransactionId(transaction.id)
                TransactionType.INBOUND -> !inboundService.existsByTransactionId(transaction.id)
            }

    private fun processStatusRequest(payload: ByteArray, source: String): Boolean {
        TODO("Not yet implemented")
    }

    private fun sendCreatedTransaction(transaction: TransactionEntity) =
            transaction.id?.let { transactionId ->
                TransactionPackage(
                        transactionId = transactionId,
                        serviceId = Protocol.Service.TRANSACTION,
                        eventId = Protocol.Event.IMPORT,
                        operationId = Protocol.Event.CREATE_TRANSACTION,
                        numberOfPackages = transaction.numberOfPackages,
                        topic = transaction.topic
                ).getCreatedTransactionPayload()?.let { payload ->
                    publishFragment(payload, transaction.topic)
                }
            }

    fun sendEmptyTransaction(topic: String) =
            TransactionPackage(
                    serviceId = Protocol.Service.TRANSACTION,
                    eventId = Protocol.Event.IMPORT,
                    operationId = Protocol.Event.EMPTY_TRANSACTION,
                    topic = topic
            ).getEmptyTransactionPayload()?.let { payload ->
                publishFragment(payload, topic)
            }


    fun sendFragmentsAvailable(transaction: TransactionEntity) =
            TransactionPackage(
                    serviceId = Protocol.Service.TRANSACTION,
                    eventId = Protocol.Event.IMPORT,
                    operationId = Protocol.Event.FRAGMENTS_AVAILABLE,
                    transactionId = transaction.id,
                    numberOfPackages = transaction.numberOfPackages
            ).getFragmentsAvailablePayload()?.let { payload ->
                publishFragment(payload, transaction.topic)
            }

    fun closeOutboundTransaction(transactionId: UUID) {
        outboundService.deleteAllByTransactionId(transactionId)
        repository.deleteById(transactionId)
    }

    fun closeInboundTransaction(transactionId: UUID) {
        inboundService.deleteAllByTransactionId(transactionId)
        repository.deleteById(transactionId)
    }

    private fun publishFragment(payload: ByteArray, topic: String) =
            communicationService.publish(Topic.MQTT_DEFAULT, topic + Topic.RESPONSE, payload)

    fun findById(id: UUID): TransactionEntity? = repository.findById(id).orElse(null)

    fun setInboundFragmentError(transaction: TransactionEntity, inbound: InboundEntity) {
        transaction.transactionStatus = TransactionStatus.ERROR
        inboundService.setFragmentError(inbound)
        repository.save(transaction)
    }

    fun findNextAvaiableInboundFragment(transaction: TransactionEntity): InboundEntity? =
            inboundService.readNextAvailableFragment(transaction.id)

    fun deleteInboundFragment(inboundId: UUID) = inboundService.deleteById(inboundId)

    fun sendError(eventId: Byte, message: String, topic: String) {
        when (eventId) {
//            Protocol.Event.IMPORT -> errorFeedbackService.sendError(Protocol.Event.EXPORT, message, topic)
//            Protocol.Event.EXPORT -> errorFeedbackService.sendError(Protocol.Event.IMPORT, message, topic)
//            else -> errorFeedbackService.sendError(eventId, message, topic)
        }
    }

    companion object {
        val logger: Logger = Logger.getLogger(TransactionService::class.qualifiedName)
    }
}
