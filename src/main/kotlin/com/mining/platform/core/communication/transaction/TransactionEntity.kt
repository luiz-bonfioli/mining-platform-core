package com.mining.platform.core.communication.transaction

import com.mining.platform.core.audit.AuditListener
import com.mining.platform.core.audit.AuditableEntity
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Where
import java.util.*
import javax.persistence.*

/**
 * The Transaction entity
 *
 * @author luiz.bonfioli
 */
@Entity
@Table(name = "transaction")
@Where(clause = "entity_status <> 'DELETED'")
@EntityListeners(AuditListener::class)
data class TransactionEntity(

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "id", unique = true, nullable = false)
        override var id: UUID? = null,

        @Enumerated(EnumType.STRING)
        @Column(name = "transaction_status", nullable = false)
        var transactionStatus: TransactionStatus,

        @Enumerated(EnumType.STRING)
        @Column(name = "transaction_type", nullable = false)
        var transactionType: TransactionType,

        @Column(name = "number_of_packages", nullable = false)
        var numberOfPackages: Int,

        @Column(name = "service_id", nullable = false)
        var serviceId: Byte,

        @Column(name = "event_id", nullable = false)
        var eventId: Byte,

        @Column(name = "topic", nullable = false)
        var topic: String

) : AuditableEntity()
