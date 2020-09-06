package com.mining.platform.core.converter

import java.nio.ByteBuffer
import java.util.*
import java.util.regex.Pattern

/**
 *
 * @author luiz.bonfioli
 */
object UUIDConverter {
    /**
     *
     * @param bytes
     * @return
     */
    fun toUUID(bytes: ByteArray): UUID {
        val buffer = ByteBuffer.wrap(bytes)
        val firstLong = buffer.long
        val secondLong = buffer.long
        return UUID(firstLong, secondLong)
    }

    /**
     *
     * @param uuid
     * @return
     */
    fun toUUID(uuid: String?): UUID {
        return UUID.fromString(uuid)
    }

    /**
     *
     * @param uuid
     * @return
     */
    fun toString(uuid: UUID): String {
        return uuid.toString()
    }

    /**
     * Convert the uuid to byte array of length 16.
     *
     * @param uuid
     * @return the byte array
     */
    fun toBytes(uuid: UUID): ByteArray {
        val buffer = ByteBuffer.wrap(ByteArray(16))
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        return buffer.array()
    }

    /**
     *
     * @return
     */
    fun toPlainString(uuid: UUID): String {
        return uuid.toString().replace("-", "")
    }

    /**
     *
     * @param uuid
     * @return
     */
    fun format(uuid: String): String {
        return uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)".toRegex(), "$1-$2-$3-$4-$5")
    }

    /**
     *
     * @param uuid
     * @return
     */
    fun isFormatValid(uuid: String?): Boolean {
        val pattern = Pattern.compile("(\\p{XDigit}{8})-(\\p{XDigit}{4})-(\\p{XDigit}{4})-(\\p{XDigit}{4})-(\\p{XDigit}+)")
        return pattern.matcher(uuid).matches()
    }
}