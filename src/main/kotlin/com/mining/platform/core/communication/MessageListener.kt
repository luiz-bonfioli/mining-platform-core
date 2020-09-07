package com.mining.platform.core.communication

/**
 *
 * @author luiz.bonfioli
 */
interface MessageListener {
    fun onMessageArrived(eventId: Byte, payload: ByteArray, source: String)
}