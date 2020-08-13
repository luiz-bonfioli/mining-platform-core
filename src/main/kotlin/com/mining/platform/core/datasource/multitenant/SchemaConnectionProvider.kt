package com.mining.platform.core.datasource.multitenant

import org.hibernate.HibernateException
import org.hibernate.cfg.Environment
import org.hibernate.engine.config.spi.ConfigurationService
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.hibernate.service.spi.ServiceRegistryAwareService
import org.hibernate.service.spi.ServiceRegistryImplementor
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger
import javax.sql.DataSource

/**
 *
 * @author luiz.bonfioli
 */
@Component
class SchemaConnectionProvider : MultiTenantConnectionProvider, ServiceRegistryAwareService {
    private var dataSource: DataSource? = null
    override fun injectServices(serviceRegistry: ServiceRegistryImplementor) {
        dataSource = serviceRegistry.getService(ConfigurationService::class.java).settings[Environment.DATASOURCE] as DataSource?
    }

    @Throws(SQLException::class)
    override fun getAnyConnection(): Connection {
        return dataSource!!.connection
    }

    @Throws(SQLException::class)
    override fun releaseAnyConnection(connection: Connection) {
        connection.close()
    }

    @Throws(SQLException::class)
    override fun getConnection(tenantIdentifier: String): Connection {
        val connection = this.anyConnection
        try {
            connection.createStatement().execute("SET search_path to $tenantIdentifier")
            logger.log(Level.INFO, "Database schema set to {0}", tenantIdentifier)
        } catch (e: SQLException) {
            throw HibernateException(
                    "Could not alter JDBC connection to specified schema [$tenantIdentifier]", e)
        }
        return connection
    }

    @Throws(SQLException::class)
    override fun releaseConnection(tenantIdentifier: String, connection: Connection) {
        try {
            connection.createStatement().execute("SET search_path to public")
        } catch (e: SQLException) {
            throw HibernateException(
                    "Could not alter JDBC connection to specified schema [$tenantIdentifier]", e)
        }
        connection.close()
    }

    override fun supportsAggressiveRelease(): Boolean {
        return true
    }

    override fun isUnwrappableAs(unwrapType: Class<*>?): Boolean {
        return false
    }

    override fun <T> unwrap(unwrapType: Class<T>): T? {
        return null
    }

    companion object {
        private const val serialVersionUID = -5733001481258865199L

        /**
         * The logger instance for this class
         */
        private val logger = Logger.getLogger(SchemaConnectionProvider::class.java.name)
    }
}