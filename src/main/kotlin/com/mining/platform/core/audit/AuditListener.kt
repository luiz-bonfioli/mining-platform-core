package com.mining.platform.core.audit

import com.mining.platform.core.context.Context
import com.mining.platform.core.context.ContextHolder
import com.mining.platform.core.utils.CalendarUtils
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.util.*
import java.util.logging.Logger
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

/**
 * Class responsible to audit the entities
 *
 * @author luiz.bonfioli
 */
open class AuditListener {

    @PrePersist
    fun beforeSave(entity: AuditableEntity) {
        audit(entity)
    }

    @PreUpdate
    fun beforeUpdate(entity: AuditableEntity) {
        audit(entity)
    }

    /**
     * This method is responsible to audit the entity
     *
     * @param entity
     */
    private fun audit(entity: AuditableEntity) {
        val utcNow = CalendarUtils.utcNow()
        entity.lastModifiedBy = UUID.randomUUID()
        entity.lastModifiedDate = utcNow
        entity.createdBy = currentUser
        entity.createdDate = utcNow
    }

    /**
     * Get the current user from HttpRequest or Messaging Queue
     *
     * @return the user uuid
     */
    private val currentUser: UUID?
        get() {
            var currentUser = userFromRequestAttributes
            if (currentUser == null) {
                currentUser = ContextHolder.context?.userId
            }
            return currentUser
        }

    /**
     * Get the current user from HttpRequest Attributes
     *
     * @return
     */
    private val userFromRequestAttributes: UUID?
        private get() {
            val requestAttributes = RequestContextHolder.getRequestAttributes()
            return if (requestAttributes != null) {
                requestAttributes.getAttribute(Context.USER_KEY, RequestAttributes.SCOPE_REQUEST) as UUID?
            } else null
        }
}