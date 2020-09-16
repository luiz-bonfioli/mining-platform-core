package com.mining.platform.core.service

import com.mining.platform.core.datasource.EntityBase
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * @param <E>
 * @author luiz.bonfioli
</E> */
interface DataService<E : EntityBase> {

    fun save(entity: E): E

    fun update(entity: E): E

    fun saveAll(entities: Collection<E>): Collection<E>

    fun updateAll(entities: Collection<E>): Collection<E>

    fun deleteById(id: UUID?)

    fun count(): Long

    fun find(id: UUID): E?

    fun findAll(pageable: Pageable): Page<E>

    fun findByParams(pageable: Pageable, search: Map<String, String>): Page<E>

    fun findByParentIdAndParams(pageable: Pageable, parentId: UUID, search: Map<String, String>): Page<E>

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