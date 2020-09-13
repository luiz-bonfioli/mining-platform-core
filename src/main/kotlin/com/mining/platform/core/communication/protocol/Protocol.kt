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
        const val TRANSACTION: Byte = 0x01
        const val COMPANY: Byte = 0x02
        const val DEVICE: Byte = 0x03
        const val EQUIPMENT: Byte = 0x04
        const val OPERATOR: Byte = 0x05
    }

    object Event {
        const val CREATE_TRANSACTION: Byte = 0x00
        const val ABORT_TRANSACTION: Byte = 0x01
        const val EMPTY_TRANSACTION: Byte = 0x02
        const val TRANSACTION_STATUS: Byte = 0x03
        const val FRAGMENTS_AVAILABLE: Byte = 0x04
        const val FRAGMENT_REQUEST: Byte = 0x05
        const val FRAGMENT_EXPORT: Byte = 0x06

        const val IMPORT: Byte = 0x07
        const val EXPORT: Byte = 0x08
        const val ABORT: Byte = 0x09
        const val NACK: Byte = 0x10
        const val ACK: Byte = 0x11

        const val COMPANY_CREATED: Byte = 0x12
        const val EQUIPMENT_LIST: Byte = 0x13
        const val OPERATOR_LIST: Byte = 0x14
    }

    object Fanout {
        const val COMPANY_EVENT = "company.event"
    }
}