package com.mining.platform.core.utils

import java.time.Instant

/**
 *
 * @author luiz.bonfioli
 */
object CalendarUtils {
    /**
     * Get the current epoch second
     *
     * @return
     */
    fun utcNow(): Long {
        return Instant.now().epochSecond
    }
}