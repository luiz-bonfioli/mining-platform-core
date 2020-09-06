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
        const val DEVICE: Byte = 0x00
        const val EQUIPMENT: Byte = 0x01
    }

    object Event {
        const val IMPORT: Byte = 0x00
        const val EXPORT: Byte = 0x01
        const val MESSAGE: Byte = 0x02
    }

    object Topic {
        const val REQUEST = ".request"
        const val RESPONSE = ".response"
        const val MQTT_DEFAULT = "amq.topic"
        const val REGISTRATION = "company_token.registration";
    }

    object Fanout {
    }
}