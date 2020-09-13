package com.mining.platform.core.communication.transaction.import

import java.util.*

interface FragmentProcessor {

    fun process(transactionId: UUID, eventId: Byte): Boolean

}