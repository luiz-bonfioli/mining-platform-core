package com.mining.platform.core.transaction.outbound

import com.mining.platform.core.audit.AuditListener
import com.mining.platform.core.audit.AuditableEntity
import com.mining.platform.core.transaction.TransactionEntity
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Where
import java.util.*
import javax.persistence.*

/**
 * The Outbound entity
 *
 * @author luiz.bonfioli
 */
@Entity
@Table(name = "outbound")
@Where(clause = "entity_status <> 'DELETED'")
@EntityListeners(AuditListener::class)
data class OutboundEntity(

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "id", unique = true, nullable = false)
        override var id: UUID? = null,

        @Column(name = "package_number", nullable = false)
        var packageNumber: Int,

        @Column(name = "content", nullable = false)
        var content: ByteArray,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "transaction_id", foreignKey = ForeignKey(name = "transaction_fk"))
        var transaction: TransactionEntity

) : AuditableEntity()
