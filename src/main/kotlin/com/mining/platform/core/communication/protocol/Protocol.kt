package com.mining.platform.core.communication.protocol

/**
 * The communication protocol definition interface
 *
 * @author luiz.bonfioli
 */
object Protocol {

    const val COMPANY_TOKEN = "company_token"
    const val DEVICE_TOKEN = "device_token"
    const val USER_TOKEN = "user_token"
    const val APPLICATION_NAME = "application_name"
    const val APPLICATION_INSTANCE = "application_instance"

    object Service {
        const val EQUIPMENT: Byte = 0x00
    }

    object Event {
        const val IMPORT: Byte = 0x45
        const val EXPORT: Byte = 0x46
        const val MESSAGE: Byte = 0x51
    }

    object Topic {
        const val REQUEST = ".request"
        const val RESPONSE = ".response"
        const val MQTT_DEFAULT = "amq.topic"
    }

    object Fanout {
    }
}