package com.mining.platform.core.infrastructure

import com.mining.platform.core.converter.UUIDConverter
import com.mining.platform.core.infrastructure.changelog.ChangeLogEntity
import com.mining.platform.core.infrastructure.changelog.ChangeLogService
import com.mining.platform.core.infrastructure.changelog.ExecutionStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional

/**
 * This class is responsible to create the database dynamically based on tenant
 * id
 *
 * @author luiz.bonfioli
 */
@Transactional
@Repository
class DatabaseManager {

    @Autowired
    private lateinit var changeLogService: ChangeLogService

    @Autowired
    private lateinit var entityManager: EntityManager

    fun databaseExists(companyToken: String): Boolean {
        return changeLogService.existsByCompanyToken(companyToken)
    }

    fun buildDatabase(companyToken: UUID): Boolean {
        try {
            val token: String = UUIDConverter.toPlainString(companyToken)
            if (!databaseExists(token)) {
                createSchema(token)
                executeStatement(token, changeLogService.loadStatement())
            }
        } catch (ex: DatabaseBuilderException) {
            logger.log(Level.SEVERE, "Error to build database: {0}.", companyToken)
            return false
        }
        return true
    }

    @Throws(DatabaseBuilderException::class)
    private fun createSchema(companyToken: String) {
        try {
            val schema = String.format("CREATE SCHEMA _%s;", companyToken)
            entityManager.createNativeQuery(schema).executeUpdate()
            logger.log(Level.INFO, "Schema created {0}", companyToken)
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Error to create schema: {0}", companyToken)
            throw DatabaseBuilderException(ex.message, ex)
        }
    }

    @JvmOverloads
    @Throws(DatabaseBuilderException::class)
    fun executeStatement(companyToken: String, statements: List<String>, version: String = "V001") {
        var currentStatement: String? = null
        try {
            for (statement in statements) {
                currentStatement = statement.replace(defaultSchema, "_$companyToken")
                entityManager.createNativeQuery(currentStatement).executeUpdate()
            }
            logExecutionStatus(ExecutionStatus.SUCCESS, companyToken, version)
        } catch (ex: Exception) {
            logExecutionStatus(ExecutionStatus.ERROR, companyToken, version)
            logger.log(Level.SEVERE, "Error to execute script: {0} \n Company token: {1}", arrayOf<Any?>(currentStatement, companyToken))
            throw DatabaseBuilderException(ex.message, ex)
        }
    }

    private fun logExecutionStatus(status: ExecutionStatus, companyToken: String, version: String) {
        try {
            val entity = ChangeLogEntity(null, version, companyToken, status, Date())
            changeLogService.save(entity)
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot log execution status.", ex)
        }
    }

    companion object {
        protected val logger: Logger = Logger.getLogger(DatabaseManager::class.qualifiedName)
        private val defaultSchema = "mining_platform"
    }
}