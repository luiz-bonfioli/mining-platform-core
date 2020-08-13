package com.mining.platform.core.service

import com.mining.platform.core.datasource.EntityBase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * @param <E>
 * @author luiz.bonfioli
</E> */
interface DataService<E : EntityBase> {
    /**
     * @param entity
     */
    fun save(entity: E): E

    /**
     * @param entity
     */
    fun update(entity: E): E

    /**
     * @param entities
     * @return
     */
    fun saveAll(entities: Collection<E>): Collection<E>

    /**
     * @param entities
     * @return
     */
    fun updateAll(entities: Collection<E>): Collection<E>

    /**
     * @param id
     * @return
     */
    fun deleteById(id: UUID?)

    /**
     * @return
     */
    fun count(): Long

    /**
     * @return
     */
    fun find(id: UUID): E?

    /**
     * @param pageable
     * @return
     */
    fun findAll(pageable: Pageable): Page<E>

    /**
     * @param pageable
     * @param search
     * @param requestParams
     * @return
     */
    // fun findAll(pageable: PageRequest, search: String?, requestParams: Map<String, String>): Page<E>

    /**
     * @return
     */
    fun findAll(): Collection<E>
}