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

    object Topic {
        const val REQUEST = ".request"
        const val RESPONSE = ".response"
        const val DIAGNOSTIC = "system.diagnostic.event"
        const val MQTT_DEFAULT = "amq.topic"
        const val COMPANY = "application_name.company.event"
        const val BASIC_DATA_SYNC = "company_token.device_token.basic.data.sync"

    }

    object Service {
        const val COMPANY: Byte = 0x00
        const val DEVICE: Byte = 0x01
        const val EQUIPMENT: Byte = 0x02
        const val OPERATOR: Byte = 0x03
    }

    object Event {
        const val IMPORT: Byte = 0x00
        const val EXPORT: Byte = 0x01
        const val COMPANY_CREATED: Byte = 0x03
        const val EQUIPMENT_LIST: Byte = 0x04
        const val OPERATOR_LIST: Byte = 0x05
    }

    object Fanout {
        const val COMPANY_EVENT = "company.event"
    }
}