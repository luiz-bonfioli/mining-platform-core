package com.mining.platform.core.datasource.transaction

import com.mining.platform.core.context.Context
import com.mining.platform.core.context.ContextHolder
import com.mining.platform.core.datasource.EntityBase
import org.hibernate.Session
import org.springframework.stereotype.Service
import java.util.logging.Level
import java.util.logging.Logger
import javax.persistence.EntityManagerFactory

/**
 * @author luiz.bonfioli
 */
@Service("TransactionManager")
class TransactionManager(private val entityManagerFactory: EntityManagerFactory) {

    val logger: Logger = Logger.getLogger(TransactionManager::class.qualifiedName)

    /**
     * @param entity
     * @return
     */
    fun <E : EntityBase> save(entity: E) {
        val context: Context? = ContextHolder.context
        ContextHolder.clear()
        val entityManager = entityManagerFactory.createEntityManager()
        val session = entityManager.unwrap(Session::class.java)
        try {
            session.transaction.begin()
            entityManager.persist(entity)
            entityManager.flush()
            entityManager.close()
            session.transaction.commit()
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot persist entity.", ex)
            session.transaction.rollback()
        } finally {
            ContextHolder.context = context
        }
    }

    /**
     * @param queryHql
     * @param entityClass
     * @param parameters
     * @return
     */
    fun <E : EntityBase> find(queryHql: String, entityClass: Class<E>, parameters: Map<String, Any>): Collection<E> {
        val context: Context? = ContextHolder.context
        ContextHolder.clear()
        var entities: Collection<E> = ArrayList()
        val entityManager = entityManagerFactory.createEntityManager()
        try {
            val query = entityManager.createQuery(queryHql, entityClass)
            for ((key, value) in parameters) {
                query.setParameter(key, value)
            }
            entities = query.resultList
            entityManager.close()
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot execute query.", ex)
        } finally {
            ContextHolder.context = context
        }
        return entities
    }
}