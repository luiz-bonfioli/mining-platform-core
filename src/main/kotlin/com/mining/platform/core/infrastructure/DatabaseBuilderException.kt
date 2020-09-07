package com.mining.platform.core.infrastructure

/**
 *
 * @author luiz.bonfioli
 */
class DatabaseBuilderException : Exception {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, ex: Exception?) : super(message, ex) {}

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 6148682643613552243L
    }
}