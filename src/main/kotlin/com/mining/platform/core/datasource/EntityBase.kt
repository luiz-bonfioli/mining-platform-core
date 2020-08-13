package com.mining.platform.core.datasource

import java.io.Serializable
import java.util.*

/**
 * The Entity Base interface
 *
 * @author luiz.bonfioli
 */
interface EntityBase : Serializable {
    /**
     * Get the entity id
     *
     * @return the entity id
     */
    /**
     * Set the entity id
     *
     * @param id - the entity id
     */
    var id: UUID?

}