package com.mining.platform.core.communication.topic


import com.mining.platform.core.Application
import com.mining.platform.core.datasource.EntityStatus
import com.mining.platform.core.datasource.transaction.TransactionManager
import com.mining.platform.core.service.AbstractService
import com.mining.platform.core.service.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

/**
 *
 * @author luiz.bonfioli
 */
@Service
class TopicService : AbstractService<TopicEntity, TopicRepository>(), DataService<TopicEntity> {

    @Autowired
    private lateinit var application: Application;

    @Autowired
    private lateinit var transactionManager: TransactionManager;

    fun findMqttInboundTopics(): Collection<TopicEntity> =
            repository.findTopics(application.name, application.instance, TopicType.MQTT_INBOUND)

    fun findMqttOutboundTopics(): Collection<TopicEntity> =
            repository.findTopics(application.name, application.instance, TopicType.MQTT_OUTBOUND)

    fun findServiceInboundTopics(): Collection<TopicEntity> =
            repository.findTopics(application.name, application.instance, TopicType.SERVICE_INBOUND)

    fun findServiceOutboundTopics(): Collection<TopicEntity> =
            repository.findTopics(application.name, application.instance, TopicType.SERVICE_OUTBOUND)

    fun create(topic: String, exchange: String, topicType: TopicType, exchangeType: ExchangeType): TopicEntity =
            create(topic, exchange, topicType, exchangeType, durable = true, autoDelete = false)

    fun create(topic: String, exchange: String, topicType: TopicType, exchangeType: ExchangeType, durable: Boolean): TopicEntity =
            create(topic, exchange, topicType, exchangeType, durable, false)

    fun create(topic: String, exchange: String, topicType: TopicType, exchangeType: ExchangeType, durable: Boolean, autoDelete: Boolean): TopicEntity =
            create(topic, exchange, topicType, exchangeType, durable, autoDelete, false)

    fun create(topic: String, exchange: String, topicType: TopicType, exchangeType: ExchangeType, durable: Boolean, autoDelete: Boolean, singleActiveConsumer: Boolean): TopicEntity {
        val topicEntity = TopicEntity().also {
            it.application = application.name
            it.topic = topic
            it.topicType = topicType
            it.exchange = exchange
            it.exchangeType = exchangeType
            it.isDurable = durable
            it.isAutoDelete = autoDelete
            it.status = EntityStatus.CREATED
            it.isSingleActiveConsumer = singleActiveConsumer
            it.instance = if (ExchangeType.INSTANCE_FANOUT_EXCHANGE === exchangeType) application.instance else null
        }

        val parameters: MutableMap<String, Any> = HashMap()
        parameters["topic"] = topic.trim { it <= ' ' }
        parameters["topicType"] = topicType

        val entityCollection: Collection<TopicEntity> = transactionManager.find(
                "from TopicEntity t where trim(t.topic) = :topic and t.topicType = :topicType",
                TopicEntity::class.java,
                parameters)

        if (entityCollection != null && entityCollection.isEmpty()) {
            transactionManager.save(topicEntity)
        }

        return topicEntity
    }
}