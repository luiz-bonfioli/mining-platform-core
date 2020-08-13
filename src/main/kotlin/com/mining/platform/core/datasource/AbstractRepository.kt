package com.mining.platform.core.datasource

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param
import java.util.*

@NoRepositoryBean
interface AbstractRepository<E : EntityBase?, ID> : JpaRepository<E, ID> {
    /**
     *
     * @param id
     * @param active
     */
    @Modifying
    @Query("update #{#entityName} entity set entity.status = :status where entity.id = :id")
    fun updateEntityStatusById(@Param("id") id: UUID?, @Param("status") status: EntityStatus)
}