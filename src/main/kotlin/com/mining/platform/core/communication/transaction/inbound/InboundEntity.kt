package com.mining.platform.core.communication.transaction.inbound

import com.mining.platform.core.audit.AuditListener
import com.mining.platform.core.audit.AuditableEntity
import com.mining.platform.core.communication.transaction.TransactionEntity
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Where
import java.util.*
import javax.persistence.*

/**
 * The Inbound entity
 *
 * @author luiz.bonfioli
 */
@Entity
@Table(name = "inbound")
@Where(clause = "entity_status <> 'DELETED'")
@EntityListeners(AuditListener::class)
data class InboundEntity(

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "id", unique = true, nullable = false)
        override var id: UUID? = null,

        @Column(name = "package_number", nullable = false)
        var packageNumber: Int,

        @Column(name = "content", nullable = false)
        var content: ByteArray,

        @Enumerated(EnumType.STRING)
        @Column(name = "inbound_status")
        var inboundStatus: InboundStatus,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "transaction_id", foreignKey = ForeignKey(name = "transaction_fk"))
        var transaction: TransactionEntity

) : AuditableEntity()
