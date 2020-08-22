package com.mining.platform.core.communication.topic

import com.mining.platform.core.datasource.AbstractRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 *
 * @author luiz.bonfioli
 */
interface TopicRepository : AbstractRepository<TopicEntity, UUID> {

    @Query("from TopicEntity topic where topic.application = :application and ( topic.instance is null or topic.instance = :instance ) and topic.topicType = :topicType")
    fun findTopics(@Param("application") application: String,
                   @Param("instance") instance: UUID?,
                   @Param("topicType") topicType: TopicType): Collection<TopicEntity>

    fun existsByTopic(topic: String): Boolean

}