package com.mining.platform.core.infrastructure.changelog

import com.mining.platform.core.service.AbstractService
import com.mining.platform.core.service.DataService
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Level


/**
 * The ChangeLog service
 *
 * @author luiz.bonfioli
 */
@Service
class ChangeLogService : AbstractService<ChangeLogEntity, ChangeLogRepository>(), DataService<ChangeLogEntity> {

    /**
     *
     * @param companyToken
     * @return
     */
    fun existsByCompanyToken(companyToken: String): Boolean {
        return repository.existsByCompanyToken(companyToken)
    }

    /**
     *
     * @return
     */
    fun loadStatement(): List<String> {
        val statements: MutableList<String> = ArrayList()
        var stringBuilder: StringBuilder = StringBuilder()
        try {
            ClassPathResource("database.sql").inputStream.bufferedReader().forEachLine {
                stringBuilder.append(it)
                if (it == "" && stringBuilder.isNotEmpty()) {
                    statements.add(stringBuilder.toString())
                    stringBuilder.setLength(0)
                }
            }
        } catch (ex: Exception) {
            logger.log(Level.SEVERE, "Cannot load Statement.", ex)
        } finally {
            stringBuilder.setLength(0)
        }
        return statements
    }

}
