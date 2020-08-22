package com.mining.platform.core.service

import com.mining.platform.core.communication.MessageListener
import kotlin.reflect.KClass

/**
 *
 * @author luiz.bonfioli
 */
object ServiceMapping {

    private val servicesMap: MutableMap<Byte, KClass<out MessageListener>> = HashMap()

    fun inject(id: Byte, service: KClass<out MessageListener>) {
        servicesMap[id] = service
    }

    fun getServiceById(serviceId: Byte): KClass<out MessageListener>? {
        return servicesMap[serviceId]
    }
}