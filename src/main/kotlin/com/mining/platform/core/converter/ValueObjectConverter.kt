package com.mining.platform.core.converter

import com.mining.platform.core.controller.ValueObject
import com.mining.platform.core.datasource.EntityBase
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * @author luiz.bonfioli
 */
object ValueObjectConverter {

    private val logger = Logger.getLogger(ValueObjectConverter::class.qualifiedName)

    fun <VO : ValueObject<E>, E : EntityBase> convert(entity: E, classVO: KClass<VO>): VO =
            classVO.createInstance().apply {
                this.entity = entity
            }

    fun <VO : ValueObject<E>, E : EntityBase> convert(entityList: Collection<E>, classVO: KClass<VO>): Collection<VO> =
            entityList.map { convert(it, classVO) }
}