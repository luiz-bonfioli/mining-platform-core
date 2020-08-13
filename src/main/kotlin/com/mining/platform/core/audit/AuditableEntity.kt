package com.mining.platform.core.audit

import com.mining.platform.core.datasource.EntityBase
import com.mining.platform.core.datasource.EntityStatus
import java.util.*
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.MappedSuperclass

/**
 * The Auditable Entity class
 *
 * @author luiz.bonfioli
 */
@MappedSuperclass
abstract class AuditableEntity : EntityBase {
    
    @Column(name = "created_by", updatable = false)
    var createdBy: UUID? = null

    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: Long = 0

    @Column(name = "last_modified_by")
    var lastModifiedBy: UUID? = null

    @Column(name = "last_modified_date", nullable = false)
    var lastModifiedDate: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_status", nullable = false)
    var status: EntityStatus? = null

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -3227373115390761183L
    }
}