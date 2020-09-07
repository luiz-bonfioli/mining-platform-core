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
    protected lateinit var repository: R

    /**
     * Save or update entity.
     *
     * @param entity
     * @return
     */
    @Transactional
    open fun saveOrUpdate(entity: E): E = if (entity.id == null) save(entity) else update(entity)

    /**
     * @param entities
     * @return
     */
    @Transactional
    open fun saveOrUpdate(entities: Collection<E>): List<E> {
        val entityList: MutableList<E> = ArrayList()
        for (entity in entities) {
            entityList.add(if (entity.id == null) save(entity) else update(entity))
        }
        return entityList
    }

    /**
     * Save and audit an entity
     *
     * @param entity
     * @return
     */
    @Transactional
    open fun save(entity: E): E {
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
    open fun update(entity: E): E {
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
    open fun saveAll(entities: Collection<E>): Collection<E> {
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
    open fun updateAll(entities: Collection<E>): List<E> {
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
    open fun find(id: UUID): E? {
        val entity = repository.findById(id)
        return if (entity.isPresent) entity.get() else null
    }

    /**
     * Check if the entity exists by its id
     *
     * @param id
     * @return
     */
    open fun existsById(id: UUID): Boolean = repository.existsById(id)

    /**
     * Find all entities
     *
     * @return
     */
    open fun findAll(): Collection<E> = repository.findAll()

    /**
     * Find all entities with pagination
     *
     * @param pageable
     * @return
     */
    open fun findAll(pageable: Pageable): Page<E> = repository.findAll(pageable)

    /**
     * Find all entities with criteria
     *
     * @param pageable
     * @param filterParams
     * @return
     */
    open fun findByParams(pageable: Pageable, search: Map<String, String>): Page<E> =
        throw MethodSearchNotImplementedException("This method should be implemented with custom query in the repository interface.")

    /**
     * Delete an entity by its id. This is a soft delete.
     *
     * @param id
     */
    @Transactional
    open fun deleteById(id: UUID?) {
        beforeDelete(id)
        repository.updateEntityStatusById(id, EntityStatus.DELETED)
        afterDelete(id)
    }

    /**
     *
     */
    @Transactional
    open fun deleteAll() {
        deleteAll(repository.findAll())
    }

    /**
     * Delete all entities. This method uses soft deletion by default.
     *
     * @param entities
     */
    @Transactional
    open fun deleteAll(entities: Collection<E>) {
        for (entity in entities) {
            deleteById(entity.id)
        }
    }

    /**
     * Count the entities
     *
     * @return
     */
    open fun count(): Long {
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
    open fun beforeSave(entity: E) {
        setEntityStatus(entity, EntityStatus.CREATED)
    }

    /**
     *
     */
    open fun beforeUpdate(entity: E) {
        setEntityStatus(entity, EntityStatus.UPDATED)
    }

    /**
     *
     */
    open fun beforeDelete(id: UUID?) {}

    /**
     *
     */
    open fun beforeSave(entities: Collection<E>) {
        setEntityStatus(entities, EntityStatus.CREATED)
    }

    /**
     *
     */
    open fun beforeUpdate(entities: Collection<E>) {
        setEntityStatus(entities, EntityStatus.UPDATED)
    }

    /**
     *
     */
    open fun afterSave(entity: E) {}

    /**
     *
     */
    open fun afterUpdate(entity: E) {}

    /**
     *
     */
    open fun afterDelete(id: UUID?) {}

    /**
     *
     */
    open fun afterSave(entities: Collection<E>) {}

    /**
     *
     */
    open fun afterUpdate(entities: Collection<E>) {}

    companion object {
        val logger: Logger = Logger.getLogger(AbstractService::class.qualifiedName)
    }
}