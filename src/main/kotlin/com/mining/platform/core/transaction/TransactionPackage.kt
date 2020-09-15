package com.mining.platform.core.transaction

import com.mining.platform.core.converter.UUIDConverter.toBytes
import java.nio.ByteBuffer
import java.util.*


class TransactionPackage(
        var serviceId: Byte,
        var eventId: Byte,
        var operationId: Byte,
        var topic: String? = null,
        var numberOfPackages: Int = 0,
        var transactionId: UUID? = null
) {

    fun getCreatedTransactionPayload(): ByteArray? =
            transactionId?.let { transactionId ->
                topic?.let { topic ->
                    ByteBuffer.allocate(19 + topic.toByteArray().size).apply {
                        put(serviceId)
                        put(eventId)
                        put(operationId)
                        put(toBytes(transactionId))
                        put(topic.toByteArray())
                    }.array()
                }
            }


    fun getAbortTransactionPayload(): ByteArray? =
            transactionId?.let { transactionId ->
                ByteBuffer.allocate(19).apply {
                    put(serviceId)
                    put(eventId)
                    put(operationId)
                    put(toBytes(transactionId))
                }.array()
            }

    fun getEmptyTransactionPayload(): ByteArray? =
            topic?.let { topic ->
                ByteBuffer.allocate(3 + topic.toByteArray().size).apply {
                    put(serviceId)
                    put(eventId)
                    put(operationId)
                    put(topic.toByteArray())
                }.array()
            }

    fun getFragmentsAvailablePayload(): ByteArray? =
            transactionId?.let { transactionId ->
                ByteBuffer.allocate(23).apply {
                    put(serviceId)
                    put(eventId)
                    put(operationId)
                    put(toBytes(transactionId))
                    putInt(numberOfPackages)
                }.array()
            }
}