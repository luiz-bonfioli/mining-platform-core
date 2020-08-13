package com.mining.platform.core.controller

import com.mining.platform.core.datasource.EntityBase
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * @author luiz.bonfioli
 */
object ValueObjectConverter {
    /**
     * The logger instance for this class
     */
    private val logger = Logger.getLogger(ValueObjectConverter::class.qualifiedName)

    /**
     * @param entity
     * @param classVO
     * @return
     */
    fun <VO : ValueObject<E>, E : EntityBase> convert(entity: E?, classVO: KClass<VO>): VO {
        val valueObject: VO = classVO.createInstance()
        if(entity != null)
            valueObject.entity = entity
        return valueObject
    }

    /**
     * @param entityList
     * @param classVO
     * @return
     */
    @JvmStatic
    fun <VO : ValueObject<E>, E : EntityBase> convert(entityList: Collection<E>, classVO: KClass<VO>): Collection<VO> {
        val valueObjectList: MutableList<VO> = ArrayList()
        try {
            for (entity in entityList) {
                val response: VO = convert(entity, classVO)
                valueObjectList.add(response)
            }
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot convert entityList to valueObjectList.", ex)
        }
        return valueObjectList
    }
}