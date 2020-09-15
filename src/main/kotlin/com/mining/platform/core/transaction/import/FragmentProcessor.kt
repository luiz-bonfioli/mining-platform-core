package com.mining.platform.core.transaction.import

import java.util.*

interface FragmentProcessor {

    fun process(transactionId: UUID, eventId: Byte): Boolean

}