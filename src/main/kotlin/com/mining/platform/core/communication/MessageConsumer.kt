package com.mining.platform.core.communication

import com.mining.platform.core.communication.topic.TopicUtils
import com.mining.platform.core.context.Context
import com.mining.platform.core.context.ContextHolder
import com.mining.platform.core.converter.UUIDConverter
import com.mining.platform.core.service.ServiceMapping
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.util.logging.Level
import java.util.logging.Logger

/**
 * The message consumer class
 *
 * @author luiz.bonfioli
 */
@Component
class MessageConsumer() : org.springframework.amqp.core.MessageListener {

    @Autowired
    private lateinit var beanFactory: AutowireCapableBeanFactory

    override fun onMessage(message: Message) {
        try {
            setContextHolder(message)
            notifyMessageArrived(message)
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot process the message arrived.", ex)
        } finally {
            ContextHolder.clear()
        }
    }

    /**
     * Set the context holder with company and user ids
     *
     * @param message
     */
    private fun setContextHolder(message: Message) {
        val consumerQueue = message.messageProperties.consumerQueue
        val companyToken = TopicUtils.getCompanyToken(consumerQueue)
        val userToken = TopicUtils.getUserToken(consumerQueue)

        if (UUIDConverter.isFormatValid(companyToken) && UUIDConverter.isFormatValid(userToken)) {
            ContextHolder.context = Context(UUIDConverter.toUUID(companyToken), UUIDConverter.toUUID(userToken))
        }
    }

    /**
     * Notify new message arrived to registered services
     *
     * @param message
     */
    private fun notifyMessageArrived(message: Message) {
        val payloadBuffer = ByteBuffer.wrap(message.body)
        val serviceId = payloadBuffer.get()
        val eventId = payloadBuffer.get()
        var content: ByteArray? = null
        val remaining = payloadBuffer.remaining()

        if (remaining != 0) {
            content = ByteArray(remaining)
            payloadBuffer[content]
        }

        val service = ServiceMapping.getServiceById(serviceId)
        if (service != null) {
            val source = message.messageProperties.consumerQueue
            val listener: MessageListener = beanFactory.getBean(service.java)
            listener.onMessageArrived(eventId, content, source)
            logger.log(Level.INFO, "Message arrived: [ $serviceId, $eventId ] ${service.qualifiedName}")
        } else {
            logger.log(Level.SEVERE, "Service not found: [ $serviceId, $eventId ]. Check the Service Mapping injection")
        }
    }

    companion object {
        /**
         * The logger instance for this class
         */
        private val logger = Logger.getLogger(MessageConsumer::class.qualifiedName)
    }
}