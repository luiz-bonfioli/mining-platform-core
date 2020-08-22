package com.mining.platform.core.communication.topic

import com.mining.platform.core.audit.AuditListener
import com.mining.platform.core.audit.AuditableEntity
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Where
import java.util.*
import javax.persistence.*

/**
 *
 * @author luiz.bonfioli
 */
@Entity
@Table(name = "topic", uniqueConstraints = [UniqueConstraint(columnNames = ["topic", "topic_type"], name = "topic_topic_type_uk")])
@Where(clause = "entity_status <> 'DELETED'")
@EntityListeners(AuditListener::class)
class TopicEntity(

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "id", unique = true)
        override var id: UUID? = null,

        @Column(name = "application", nullable = false)
        var application: String? = null,

        @Column(name = "instance")
        var instance: UUID? = null,

        @Column(name = "topic", nullable = false)
        var topic: String? = null,

        @Column(name = "exchange", nullable = false)
        var exchange: String? = null,

        @Enumerated(EnumType.STRING)
        @Column(name = "topic_type", nullable = false)
        var topicType: TopicType? = null,

        @Enumerated(EnumType.STRING)
        @Column(name = "exchange_type", nullable = false)
        var exchangeType: ExchangeType? = null,

        @Column(name = "auto_delete", nullable = false)
        var isAutoDelete: Boolean = false,

        @Column(name = "durable", nullable = false)
        var isDurable: Boolean = false,

        @Column(name = "single_active_consumer", nullable = false)
        var isSingleActiveConsumer: Boolean = false

) : AuditableEntity()