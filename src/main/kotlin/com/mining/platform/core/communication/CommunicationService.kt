package com.mining.platform.core.communication

import com.mining.platform.core.communication.topic.ExchangeType
import com.mining.platform.core.communication.topic.TopicEntity
import com.mining.platform.core.communication.topic.TopicService
import com.mining.platform.core.communication.topic.TopicUtils.toStringArray
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.util.logging.Level
import java.util.logging.Logger

@Service
@DependsOn("application")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class CommunicationService {

    private val logger = Logger.getLogger(CommunicationService::class.qualifiedName)

    private lateinit var container: DirectMessageListenerContainer

    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    private lateinit var connectionFactory: ConnectionFactory

    @Autowired
    private lateinit var rabbitAdmin: RabbitAdmin

    @Autowired
    private lateinit var topicService: TopicService

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun rabbitAdmin(): RabbitAdmin {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    fun container(connectionFactory: ConnectionFactory, consumer: MessageConsumer): DirectMessageListenerContainer {
        container = DirectMessageListenerContainer()
        container.connectionFactory = connectionFactory
        container.setMessageListener(consumer)
        subscribeToMqttTopics(topicService.findMqttInboundTopics())
        subscribeToServiceTopics(topicService.findServiceInboundTopics())
        return container
    }

    @Bean
    fun consumer(): MessageConsumer {
        return MessageConsumer()
    }

    fun initialize() {
        createMqttInboundTopics(topicService.findMqttInboundTopics())
        createMqttOutboundTopics(topicService.findMqttOutboundTopics())
        createServiceInboundTopics(topicService.findServiceInboundTopics())
        createServiceOutboundTopics(topicService.findServiceOutboundTopics())
    }

    fun createMqttInboundTopics(inboundTopics: Collection<TopicEntity>) {
        try {
            for (topic in inboundTopics) {
                declareDurableQueue(topic.topic, topic.exchange, topic.exchangeType)
            }
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot create device inbound queues on starting server.", ex)
        }
    }

    fun createMqttOutboundTopics(outboundTopics: Collection<TopicEntity>) {
        try {
            for (topic in outboundTopics) {
                declareDurableQueue(topic.topic, topic.exchange, topic.exchangeType)
            }
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot create device outbound queues on starting server.", ex)
        }
    }

    fun createServiceInboundTopics(serviceInboundTopics: Collection<TopicEntity>) {
        try {
            for (topic in serviceInboundTopics) {
                declareDurableQueue(topic.topic, topic.exchange, topic.exchangeType)
            }
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot create service inbound queues on starting server.", ex)
        }
    }

    fun createServiceOutboundTopics(serviceOutboundTopics: Collection<TopicEntity>) {
        try {
            for (topic in serviceOutboundTopics) {
                declareDurableQueue(topic.topic, topic.exchange, topic.exchangeType)
            }
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot create service outbound queues on starting server.", ex)
        }
    }

    private fun declareDurableQueue(queueName: String?, exchangeName: String?, exchangeType: ExchangeType?) {
        try {
            val queue = QueueBuilder.durable(queueName).build()
            rabbitAdmin.declareQueue(queue)
            when (exchangeType) {
                ExchangeType.TOPIC_EXCHANGE -> {
                    val topicExchange = TopicExchange(exchangeName)
                    rabbitAdmin.declareExchange(topicExchange)
                    rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with(queueName))
                }
                ExchangeType.SERVICE_FANOUT_EXCHANGE, ExchangeType.INSTANCE_FANOUT_EXCHANGE -> {
                    val fanoutExchange = FanoutExchange(exchangeName)
                    rabbitAdmin.declareExchange(fanoutExchange)
                    rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(fanoutExchange))
                }
            }
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot declare queue: $queueName", ex)
        }
    }

    fun subscribeToMqttTopics(deviceInboundTopics: Collection<TopicEntity>) {
        try {
            container.addQueueNames(*toStringArray(deviceInboundTopics))
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot subscribe to device topics.", ex)
        }
    }

    fun subscribeToServiceTopics(serviceInboundTopics: Collection<TopicEntity>) {
        try {
            container.addQueueNames(*toStringArray(serviceInboundTopics))
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot subscribe to service topics.", ex)
        }
    }

    fun publish(exchange: String, routingKey: String, payload: ByteArray) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, payload)
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot publish message to queue: $exchange", ex)
        }
    }

    fun publish(exchange: String, routingKey: String, service: Byte, event: Byte, payload: ByteArray) {
        publish(exchange, routingKey, byteArrayOf(service, event).plus(payload))
    }
}