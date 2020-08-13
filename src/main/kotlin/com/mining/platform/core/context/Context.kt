package com.mining.platform.core.context

import java.util.*

/**
 *
 * @author luiz.bonfioli
 */
class Context(val tenantId: UUID, val userId: UUID) {

    companion object {
        const val TENANT_KEY = "X-Company-Tenant"
        const val USER_KEY = "X-User"
    }

}