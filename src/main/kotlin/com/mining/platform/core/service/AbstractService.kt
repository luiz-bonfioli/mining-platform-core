package com.mining.platform.core.service

import com.mining.platform.core.audit.AuditableEntity
import com.mining.platform.core.datasource.AbstractRepository
import com.mining.platform.core.datasource.EntityBase
import com.mining.platform.core.datasource.EntityStatus
import com.mining.platform.core.exception.MethodSearchNotImplementedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * This class provides the common service operations
 *
 * @param <E>
 * @author luiz.bonfioli
</E> */
abstract class AbstractService<E : EntityBase, R : AbstractRepository<E, UUID>> {
    /**
     * @return
     */
    /**
     * Injection of repository instance
     */
    @Autowired
    private lateinit var repository: R

    /**
     * Save or update entity.
     *
     * @param entity
     * @return
     */
    @Transactional
    fun saveOrUpdate(entity: E): E {
        return if (entity.id == null) save(entity) else update(entity)
    }

    /**
     * @param entities
     * @return
     */
    @Transactional
    fun saveOrUpdate(entities: Collection<E>): List<E> {
        val entityList: MutableList<E> = ArrayList()
        for (entity in entities) {
            entityList.add(if (entity.id == null) save(entity) else update(entity))
        }
        return entityList
    }

    /**
     * Save and flush audit an entity
     *
     * @param entity
     * @return
     */
    @Transactional
    fun saveAndFlush(entity: E): E {
        beforeSave(entity)
        val saved = repository.saveAndFlush(entity)
        afterSave(saved)
        return saved
    }

    /**
     * Save and flush audit an entity
     *
     * @param entity
     * @return
     */
    @Transactional
    fun updateAndFlush(entity: E): E {
        beforeUpdate(entity)
        val saved = repository.saveAndFlush(entity)
        afterUpdate(saved)
        return saved
    }

    /**
     * Save and audit an entity
     *
     * @param entity
     * @return
     */
    @Transactional
    fun save(entity: E): E {
        beforeSave(entity)
        val saved = repository.save(entity)
        afterSave(saved)
        return saved
    }

    /**
     * Update and audit an entity
     *
     * @param entity
     * @return
     */
    @Transactional
    fun update(entity: E): E {
        beforeUpdate(entity)
        val updated = repository.save(entity)
        afterUpdate(updated)
        return updated
    }

    /**
     * Save and audit an entity list
     *
     * @param entities
     * @return
     */
    @Transactional
    fun saveAll(entities: Collection<E>): Collection<E> {
        beforeSave(entities)
        val entityList: MutableList<E> = ArrayList()
        for (entity in entities) {
            val updated = repository.save(entity)
            entityList.add(updated)
        }
        afterSave(entityList)
        return entityList
    }

    /**
     * Update and audit an entity list
     *
     * @param entities
     * @return
     */
    @Transactional
    fun updateAll(entities: Collection<E>): List<E> {
        beforeUpdate(entities)
        val entityList: MutableList<E> = ArrayList()
        for (entity in entities) {
            val updated = repository.save(entity)
            entityList.add(updated)
        }
        afterUpdate(entityList)
        return entityList
    }

    /**
     * Find an entity by its id
     *
     * @param id
     * @return
     */
    fun find(id: UUID): E? {
        val entity = repository.findById(id)
        return if (entity.isPresent) entity.get() else null
    }

    /**
     * Get the entity reference by its id
     *
     * @param id
     * @return
     */
    fun getOne(id: UUID): E {
        return repository.getOne(id)
    }

    /**
     * Check if the entity exists by its id
     *
     * @param id
     * @return
     */
    fun existsById(id: UUID): Boolean {
        return repository.existsById(id)
    }

    /**
     * Find all entities
     *
     * @return
     */
    fun findAll(): List<E> {
        return repository.findAll()
    }

    /**
     * Find all entities with pagination
     *
     * @param pageable
     * @return
     */
    fun findAll(pageable: Pageable): Page<E> {
        return repository.findAll(pageable)
    }

    /**
     * Find all entities with criteria
     *
     * @param pageable
     * @param search
     * @param filterParams
     * @return
     */
    fun findAll(pageable: PageRequest, search: String?, filterParams: Map<String, String>): Page<E> {
        throw MethodSearchNotImplementedException("This method should be implemented with custom query in the repository interface.")
    }


    /**
     * Delete an entity by its id. This is a soft delete.
     *
     * @param id
     */
    @Transactional
    fun deleteById(id: UUID?) {
        beforeDelete(id)
        repository.updateEntityStatusById(id, EntityStatus.DELETED)
        afterDelete(id)
    }

    /**
     *
     */
    @Transactional
    fun deleteAll() {
        deleteAll(repository.findAll())
    }

    /**
     * Delete all entities. This method uses soft deletion by default.
     *
     * @param entities
     */
    @Transactional
    fun deleteAll(entities: Collection<E>) {
        for (entity in entities) {
            deleteById(entity.id)
        }
    }

    /**
     * Count the entities
     *
     * @return
     */
    fun count(): Long {
        return repository.count()
    }

    /**
     * @param entity
     * @param entityStatus
     */
    private fun setEntityStatus(entity: E, entityStatus: EntityStatus) {
        if (entity is AuditableEntity) {
            (entity as AuditableEntity).status = entityStatus
        } else {
            logger.log(Level.SEVERE, "The entity isn't auditable: " + entity::class.qualifiedName)
        }
    }

    /**
     * @param entities
     * @param entityStatus
     */
    private fun setEntityStatus(entities: Collection<E>?, entityStatus: EntityStatus) {
        if (entities != null && !entities.isEmpty()) {
            for (entity in entities) {
                setEntityStatus(entity, entityStatus)
            }
        }
    }

    /**
     *
     */
    protected fun beforeSave(entity: E) {
        setEntityStatus(entity, EntityStatus.CREATED)
    }

    /**
     *
     */
    protected fun beforeUpdate(entity: E) {
        setEntityStatus(entity, EntityStatus.UPDATED)
    }

    /**
     *
     */
    protected fun beforeDelete(id: UUID?) {}

    /**
     *
     */
    protected fun beforeSave(entities: Collection<E>) {
        setEntityStatus(entities, EntityStatus.CREATED)
    }

    /**
     *
     */
    protected fun beforeUpdate(entities: Collection<E>) {
        setEntityStatus(entities, EntityStatus.UPDATED)
    }

    /**
     *
     */
    protected fun afterSave(entity: E) {}

    /**
     *
     */
    protected fun afterUpdate(entity: E) {}

    /**
     *
     */
    protected fun afterDelete(id: UUID?) {}

    /**
     *
     */
    protected fun afterSave(entities: Collection<E>) {}

    /**
     *
     */
    protected fun afterUpdate(entities: Collection<E>) {}

    companion object {
        /**
         * The logger instance for this class
         */
        protected val logger = Logger.getLogger(AbstractService::class.qualifiedName)
    }
}