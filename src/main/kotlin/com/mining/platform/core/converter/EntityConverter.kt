package com.mining.platform.core.converter

import com.mining.platform.core.controller.ValueObject
import com.mining.platform.core.datasource.EntityBase
import java.util.logging.Logger

/**
 *
 * @author luiz.bonfioli
 */
object EntityConverter {
    /**
     * The logger instance for this class
     */
    internal val logger = Logger.getLogger(EntityConverter::class.qualifiedName)

    /**
     *
     * @param vo
     * @return
     */
    fun <VO : ValueObject<E>, E : EntityBase> convert(vo: VO): E = vo.entity

    /**
     *
     * @param voList
     * @return
     */
    fun <VO : ValueObject<E>, E : EntityBase> convert(voList: Collection<VO>): Collection<E> =
            voList.map { convert(it) }
}