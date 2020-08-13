package com.mining.platform.core.datasource.multitenant

import com.mining.platform.core.context.ContextHolder
import com.mining.platform.core.converter.UUIDConverter
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.util.*

/**
 *
 * @author luiz.bonfioli
 */
class ContextTenantIdentifierResolver : CurrentTenantIdentifierResolver {

    override fun resolveCurrentTenantIdentifier(): String {
        val requestAttributes = RequestContextHolder.getRequestAttributes()
        if (requestAttributes != null) {
            val tenantId = requestAttributes.getAttribute(MultiTenant.TENANT_KEY,
                    RequestAttributes.SCOPE_REQUEST) as UUID?
            if (tenantId != null) {
                return toSchemaString(tenantId)
            }
        }
        return ContextHolder.context?.let { toSchemaString(ContextHolder.context!!.tenantId) }
                ?: MultiTenant.DEFAULT_TENANT
    }

    private fun toSchemaString(tenantId: UUID): String {
        return "_" + UUIDConverter.toPlainString(tenantId)
    }

    override fun validateExistingCurrentSessions(): Boolean {
        return true
    }
}