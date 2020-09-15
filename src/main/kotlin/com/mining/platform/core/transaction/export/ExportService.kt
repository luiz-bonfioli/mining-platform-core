package com.mining.platform.core.transaction.export

import com.mining.platform.core.communication.CommunicationService
import com.mining.platform.core.communication.protocol.Protocol
import com.mining.platform.core.communication.protocol.Protocol.Topic
import com.mining.platform.core.transaction.TransactionHeader
import com.mining.platform.core.transaction.TransactionService
import com.mining.platform.core.transaction.outbound.OutboundEntity
import com.mining.platform.core.transaction.outbound.OutboundService
import com.mining.platform.core.converter.UUIDConverter.toUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


/**
 * The Export service
 *
 * @author luiz.bonfioli
 */
@Service
class ExportService {

    @Autowired
    private lateinit var communicationService: CommunicationService

    @Autowired
    private lateinit var transactionService: TransactionService

    @Autowired
    private lateinit var outboundService: OutboundService

    private val nackControl: MutableMap<UUID, Int> = Collections.synchronizedMap(HashMap<UUID, Int>())

    fun process(payload: ByteArray?): Boolean {
        try {
            val buffer: ByteBuffer = ByteBuffer.wrap(payload)
            val eventId: Byte = buffer.get()
            val content = ByteArray(buffer.remaining())
            buffer.get(content)
            when (eventId) {
                Protocol.Event.FRAGMENT_REQUEST -> {
                    onFragmentRequest(content)
                    logger.log(Level.INFO, "#### TRANSACTION: FRAGMENT_REQUEST")
                }
                Protocol.Event.ACK -> {
                    onAck(content)
                    logger.log(Level.INFO, "#### TRANSACTION: ACK")
                }
                Protocol.Event.NACK -> {
                    onNack(content)
                    logger.log(Level.INFO, "#### TRANSACTION: NACK")
                }
                Protocol.Event.ABORT -> {
                    onAbort(content)
                    logger.log(Level.INFO, "#### TRANSACTION: ABORT")
                }
                else -> {
                    logger.log(Level.WARNING, "Event {0} not found", eventId)
                    return false
                }
            }
        } catch (ex: Exception) {
            logger.log(Level.WARNING, "Cannot process the export", ex)
            return false
        }
        return true
    }

    fun onFragmentRequest(payload: ByteArray) {
        val transactionId = toUUID(payload)
        val outbound: OutboundEntity = outboundService.readNextFragment(transactionId)
        sendFragment(outbound)
    }

    /**
     *
     * @param payload
     */
    fun onAck(payload: ByteArray) {
        try {
            val header = TransactionHeader()
            header.build(payload)
            //TODO
//            outboundService.deleteById(header.outboundId)
//            val outbound: OutboundEntity = outboundService
//                    .readNextFragment(header.transactionId)
//            if (outbound != null) {
//                sendFragment(outbound)
//            } else {
//                transactionService.closeOutboundTransaction(header.transactionId)
//            }
        } catch (ex: java.lang.Exception) {
            logger.log(Level.SEVERE, "Cannot process the ack request.", ex)
        }
    }

    /**
     *
     * @param payload
     */
    fun onNack(payload: ByteArray) {
        try {
            val header = TransactionHeader()
            header.build(payload)
            val control = header.transactionId?.let {
                incrementNackControl(it)
            }
//            TODO val outbound = header.outboundId?.let { outboundService.findById(it) }
//            if (control < MAX_NACK_REQUESTS) {
//                sendFragment(outbound)
//            } else {
//                nackControl.remove(header.transactionId)
//                transactionService.closeOutboundTransaction(header.transactionId)
//                sendClosedTransaction(header.transactionId, outbound.transaction.topic)
//            }
        } catch (ex: java.lang.Exception) {
            logger.log(Level.SEVERE, "Cannot process the nack request.", ex)
        }
    }

    /**
     *
     * @param payload
     */
    fun onAbort(payload: ByteArray) {
        try {
            val header = TransactionHeader()
            header.build(payload)
            header.transactionId?.let { transactionService.closeOutboundTransaction(it) }
        } catch (ex: java.lang.Exception) {
            logger.log(Level.SEVERE, "Cannot process the abort request.", ex)
        }
    }

    /**
     *
     * @param transactionId
     * @param topic
     */
    private fun sendClosedTransaction(transactionId: UUID, topic: String) {
//      TODO  try {
//            val transactionPackage = TransactionPackage()
//            transactionPackage.serviceId = Protocol.Service.TRANSACTION
//            transactionPackage.eventId = Protocol.Event.IMPORT
//            transactionPackage.operationId = Protocol.Event.ABORT_TRANSACTION
//            transactionPackage.transactionId = transactionId
//            val payload = transactionPackage.getAbortTransactionPayload()
//            payload?.let { publishFragment(it, topic) }
//        } catch (ex: java.lang.Exception) {
//            logger.log(Level.WARNING, "Error to send created transaction", ex)
//        }
    }

    /**
     *
     * @param outbound
     */
    fun sendFragment(outbound: OutboundEntity) {
//     TODO   try {
//            val header = TransactionHeader()
//            header.setServiceId(Protocol.Service.TRANSACTION)
//            header.setEventId(Protocol.Event.IMPORT)
//            header.setOperationId(Protocol.Event.FRAGMENT_RESPONSE)
//            header.setTopic(outbound.transaction.topic)
//            header.setTransactionId(outbound.transaction.id)
//            header.setOutboundId(outbound.id)
//            header.setPackageNumber(outbound.packageNumber)
//            header.setNumberOfPackages(outbound.transaction.numberOfPackages)
//            header.setContent(outbound.content)
//            publishFragment(header)
//        } catch (ex: java.lang.Exception) {
//            logger.log(Level.WARNING, "Error to send fragment", ex)
//        }
    }

    private fun incrementNackControl(transactionId: UUID): Int {
        if (!nackControl.containsKey(transactionId)) {
            nackControl.put(transactionId, 0)
        }
        val control = 0 // TODO nackControl[transactionId] + 1
        nackControl.put(transactionId, control)
        return control
    }

    private fun publishFragment(header: TransactionHeader) {
        try {
            val payload: ByteArray = header.toByteArray()
            header.topic?.let { publishFragment(payload, it) }
        } catch (ex: java.lang.Exception) {
            logger.log(Level.WARNING, "Error to publish fragment", ex)
        }
    }

    private fun publishFragment(payload: ByteArray, topic: String) {
        communicationService.publish(Topic.MQTT_DEFAULT, topic + Topic.RESPONSE, payload)
    }

    companion object {
        val logger: Logger = Logger.getLogger(ExportService::class.qualifiedName)
        private const val MAX_NACK_REQUESTS = 3
    }
}