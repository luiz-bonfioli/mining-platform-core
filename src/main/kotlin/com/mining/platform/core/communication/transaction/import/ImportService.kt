package com.mining.platform.core.communication.transaction.import

import com.mining.platform.core.communication.CommunicationService
import com.mining.platform.core.communication.protocol.Protocol
import com.mining.platform.core.communication.protocol.Protocol.Topic
import com.mining.platform.core.communication.transaction.TransactionEntity
import com.mining.platform.core.communication.transaction.TransactionService
import com.mining.platform.core.communication.transaction.inbound.InboundEntity
import com.mining.platform.core.communication.transaction.inbound.InboundService
import com.mining.platform.core.communication.transaction.inbound.InboundStatus
import com.mining.platform.core.converter.UUIDConverter.toBytes
import com.mining.platform.core.converter.UUIDConverter.toUUID
import com.mining.platform.core.service.ServiceMapping.getServiceById
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


/**
 * The Import service
 *
 * @author luiz.bonfioli
 */
@Service
class ImportService {

    @Autowired
    private lateinit var beanFactory: AutowireCapableBeanFactory

    @Autowired
    private lateinit var communicationService: CommunicationService

    @Autowired
    private lateinit var transactionService: TransactionService

    @Autowired
    private lateinit var inboundService: InboundService

    fun process(payload: ByteArray): Boolean {
        var success = false
        try {
            val buffer: ByteBuffer = ByteBuffer.wrap(payload)
            val eventId: Byte = buffer.get()
            val content = ByteArray(buffer.remaining())
            buffer.get(content)
            when (eventId) {
                Protocol.Event.CREATE_TRANSACTION -> success = createTransaction(content)
                Protocol.Event.FRAGMENT_EXPORT -> success = saveFragment(content)
                else -> logger.log(Level.SEVERE, "No matches to the event id {0}", eventId)
            }
        } catch (ex: Exception) {
            logger.log(Level.WARNING, "Cannot process the import", ex)
        }
        return success
    }

    private fun create(transactionId: UUID, topic: String, serviceId: Byte, eventId: Byte,
                       numberOfPackages: Int): TransactionEntity {
        return transactionService.create(transactionId, topic, serviceId, eventId,
                numberOfPackages)
    }

    private fun saveFragment(payload: ByteArray): Boolean {
        var success = true
        val buffer = ByteBuffer.wrap(payload)
        val transactionIdArray = ByteArray(16)
        buffer[transactionIdArray]
        val transactionId = toUUID(transactionIdArray)
        val outboundIdArray = ByteArray(16)
        buffer[outboundIdArray]
        val outboundId = toUUID(outboundIdArray)
        val packageNumber = buffer.int
        val content = ByteArray(buffer.remaining())
        buffer[content]
        transactionService.findById(transactionId)?.let { transaction ->
            saveInbound(transaction, packageNumber, content)
            sendAck(transaction, outboundId)
            if (packageNumber == transaction.numberOfPackages) {
                success = processTransaction(transaction)
            }
        }

        return success
    }

    private fun processTransaction(transaction: TransactionEntity): Boolean {
        val service = getServiceById(transaction.serviceId)
        var success = false
        if (service != null) {
            val processor = beanFactory.getBean(service.java)
            //TODO      success = processor.onMessageArrived(transaction.id, transaction.eventId)
            if (!success) {
                logger.log(Level.SEVERE, "Error to process transaction {0}", transaction.id)
            }
        }
        return success
    }

    private fun saveInbound(transaction: TransactionEntity, packageNumber: Int,
                            content: ByteArray) {
        InboundEntity(
                packageNumber = packageNumber,
                content = content,
                inboundStatus = InboundStatus.AVAIABLE,
                transaction = transaction

        ).let {
            inboundService.save(it)
        }
    }

    private fun sendAck(transaction: TransactionEntity, outboundId: UUID) {
        val buffer = ByteBuffer.allocate(35)
        buffer.put(Protocol.Service.TRANSACTION)
        buffer.put(Protocol.Event.EXPORT)
        buffer.put(Protocol.Event.ACK)
        buffer.put(transaction.id?.let { toBytes(it) })
        buffer.put(toBytes(outboundId))
        publishFragment(buffer.array(), transaction.topic)
    }

    private fun createTransaction(payload: ByteArray): Boolean {
        var success = true
        try {
            val buffer = ByteBuffer.wrap(payload)
            val transactionIdArray = ByteArray(16)
            buffer[transactionIdArray]
            val transactionId = toUUID(transactionIdArray)
            val transactionServiceId = buffer.get()
            val transactionEventId = buffer.get()
            val numberOfPackages = buffer.int
            val topicArray = ByteArray(buffer.remaining())
            buffer[topicArray]
            val topic = String(topicArray)
            val transaction = create(transactionId, topic, transactionServiceId,
                    transactionEventId,
                    numberOfPackages)
            if (transaction == null) {
                success = false
                logger.log(Level.SEVERE, "Error to create transaction. Transaction is null")
            }
        } catch (ex: java.lang.Exception) {
            logger.log(Level.SEVERE, "Error to create transaction.", ex)
            success = false
        }
        return success
    }

    private fun publishFragment(payload: ByteArray, topic: String) {
        communicationService.publish(Topic.MQTT_DEFAULT, topic + Topic.RESPONSE, payload)
    }

    companion object {
        val logger: Logger = Logger.getLogger(ImportService::class.qualifiedName)
    }

}