package com.mining.platform.core.transaction

import com.mining.platform.core.converter.UUIDConverter.toBytes
import com.mining.platform.core.converter.UUIDConverter.toUUID
import java.nio.ByteBuffer
import java.util.*


internal class TransactionHeader(
        var serviceId: Byte = 0,
        var eventId: Byte = 0,
        var operationId: Byte = 0,
        var content: ByteArray = ByteArray(0),
        var packageNumber: Int = 0,
        var numberOfPackages: Int = 0,
        var outboundId: UUID? = null,
        var inboundId: UUID? = null,
        var transactionId: UUID? = null,
        var topic: String? = null
) {

    fun build(payload: ByteArray?) {
        val buffer: ByteBuffer = ByteBuffer.wrap(payload)
        val transactionIdArray = ByteArray(16)
        buffer.get(transactionIdArray)
        transactionId = toUUID(transactionIdArray)
        val outboundIdArray = ByteArray(16)
        buffer.get(outboundIdArray)
        outboundId = toUUID(outboundIdArray)
        if (buffer.remaining() > 0) {
            content = ByteArray(buffer.remaining())
            buffer.get(content)
        }
    }

    fun toByteArray(): ByteArray =
            content?.let {
                ByteBuffer.allocate(43 + content.size).apply {
                    put(serviceId)
                    put(eventId)
                    put(operationId)
                    put(transactionId?.let { transactionId -> toBytes(transactionId) })
                    put(outboundId?.let { outboundId -> toBytes(outboundId) })
                    putInt(packageNumber)
                    putInt(numberOfPackages)
                    put(content)
                }.array()
            }
}