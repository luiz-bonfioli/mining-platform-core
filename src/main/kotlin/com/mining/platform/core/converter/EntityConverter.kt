package com.mining.platform.core.converter

import com.mining.platform.core.controller.ValueObject
import com.mining.platform.core.datasource.EntityBase
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 *
 * @author luiz.bonfioli
 */
object EntityConverter {
    /**
     * The logger instance for this class
     */
    internal val logger = Logger.getLogger(EntityConverter::class.java.name)

    /**
     *
     * @param vo
     * @return
     */
    fun <VO : ValueObject<E>?, E : EntityBase?> convert(vo: VO): E? {
        if (vo != null) {
            return vo.entity
        }
        logger.log(Level.WARNING, "vo is null.")
        return null
    }

    /**
     *
     * @param voList
     * @return
     */
	@JvmStatic
	fun <VO : ValueObject<E>?, E : EntityBase?> convert(voList: Collection<VO>?): List<E> {
        val valueObjectList: MutableList<E> = ArrayList()
        if (voList != null) {
            for (vo in voList) {
                val response: E? = convert(vo)
                if (response != null) {
                    valueObjectList.add(response)
                }
            }
        } else {
            logger.log(Level.WARNING, "voList is null.")
        }
        return valueObjectList
    }
}