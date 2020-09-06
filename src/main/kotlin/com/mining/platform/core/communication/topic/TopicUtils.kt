package com.mining.platform.core.communication.topic

import com.mining.platform.core.communication.protocol.Protocol
import java.util.*

/**
 *
 * @author luiz.bonfioli
 */
object TopicUtils {
    /**
     *
     */
    private const val EMPTY = ""

    /**
     *
     */
    private const val QUEUE_SEPARATOR = "."

    /**
     *
     */
    private const val COMPANY_TOKEN_INDEX = 0

    /**
     *
     */
    private const val DEVICE_TOKEN_INDEX = 1

    /**
     *
     */
    private const val USER_TOKEN_INDEX = 2

    /**
     *
     * @param topicTemplate
     * @param companyToken
     * @return
     */
    @JvmOverloads
    fun formatTopic(topicTemplate: String, companyToken: String, deviceToken: String? = null, userToken: String? = null): String {
        var topic: String = topicTemplate.replace(Protocol.COMPANY_TOKEN, companyToken)
        if (deviceToken != null) topic = topic.replace(Protocol.DEVICE_TOKEN, deviceToken)
        if (userToken != null) topic = topic.replace(Protocol.USER_TOKEN, userToken)
        return topic
    }

    /**
     *
     * @param topicTemplate
     * @param companyToken
     * @param deviceToken
     * @param userToken
     * @return
     */
    fun formatTopic(topicTemplate: String, companyToken: UUID, deviceToken: UUID, userToken: UUID): String {
        return formatTopic(topicTemplate, companyToken.toString(), deviceToken.toString(), userToken.toString())
    }

    /**
     *
     * @param topicTemplate
     * @param companyToken
     * @param deviceToken
     * @return
     */
    fun formatTopic(topicTemplate: String, companyToken: UUID, deviceToken: UUID): String {
        return formatTopic(topicTemplate, companyToken.toString(), deviceToken.toString())
    }

    /**
     *
     * @param topicTemplate
     * @param companyToken
     * @param application
     * @return
     */
    fun formatTopic(topicTemplate: String, companyToken: UUID, application: String): String {
        val topic = formatTopic(topicTemplate, companyToken.toString())
        return formatTopicByApplication(topic, application, null)
    }

    /**
     *
     * @param topicTemplate
     * @param companyToken
     * @return
     */
    fun formatTopic(topicTemplate: String, companyToken: UUID): String {
        return formatTopic(topicTemplate, companyToken.toString())
    }
    /**
     *
     * @param topicTemplate
     * @param application
     * @param uuid
     * @return
     */
    /**
     *
     * @param topicTemplate
     * @param application
     * @param uuid
     * @return
     */
    @JvmOverloads
    fun formatTopicByApplication(topicTemplate: String, application: String, uuid: UUID? = null): String {
        var topic: String = topicTemplate.replace(Protocol.APPLICATION_NAME, application)
        if (uuid != null) {
            topic = topic.replace(Protocol.APPLICATION_INSTANCE, uuid.toString())
        }
        return topic
    }

    /**
     * Convert the topic list to string array
     *
     * @param topics
     * @return the topics array of string
     */
    fun toStringArray(topics: Collection<TopicEntity>): Array<String?> {
        return topics.map { it.topic }.toTypedArray()
    }

    /**
     *
     * @param queueName
     * @return
     */
    fun getCompanyToken(queueName: String): String? =
            queueName.split(QUEUE_SEPARATOR).getOrNull(COMPANY_TOKEN_INDEX)

    /**
     *
     * @param queueName
     * @return
     */
    fun getDeviceToken(queueName: String): String? =
            queueName.split(QUEUE_SEPARATOR).getOrNull(DEVICE_TOKEN_INDEX)

    /**
     *
     * @param queueName
     * @return
     */
    fun getUserToken(queueName: String): String? =
            queueName.split(QUEUE_SEPARATOR).getOrNull(USER_TOKEN_INDEX)

    /**
     *
     * @param topic
     * @return
     */
    fun convertToResponse(topic: String): String =
            topic.replace(Protocol.Topic.REQUEST, "") + Protocol.Topic.RESPONSE
}