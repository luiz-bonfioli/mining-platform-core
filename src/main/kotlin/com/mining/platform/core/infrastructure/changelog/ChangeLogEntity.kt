package com.mining.platform.core.infrastructure.changelog

import com.mining.platform.core.audit.AuditListener
import com.mining.platform.core.audit.AuditableEntity
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Where
import java.util.*
import javax.persistence.*

/**
 * The ChangeLog entity
 *
 * @author luiz.bonfioli
 */
@Entity
@Table(name = "change_log")
@Where(clause = "entity_status <> 'DELETED'")
@EntityListeners(AuditListener::class)
data class ChangeLogEntity(

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "id", unique = true, nullable = false)
        override var id: UUID? = null,

        @Column(name = "version", nullable = false)
        var version: String,

        @Column(name = "company_token", nullable = false)
        var companyToken: String,

        @Enumerated(EnumType.STRING)
        @Column(name = "execution_status", nullable = false)
        var executionStatus: ExecutionStatus,

        @Temporal(value = TemporalType.TIMESTAMP)
        @Column(name = "execution_date", nullable = false)
        var executionDate: Date

) : AuditableEntity()
