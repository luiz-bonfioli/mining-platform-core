package com.mining.platform.core

import com.mining.platform.core.converter.UUIDConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.io.*
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.annotation.PostConstruct

/**
 *
 * @author luiz.bonfioli
 */
@Service("application")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class Application {

    private val logger = Logger.getLogger(Application::class.qualifiedName)

    @Value("\${spring.application.name}")
    val name: String = ""

    var instance: UUID? = null

    @PostConstruct
    private fun initialize() {
        generateInstanceId()
    }

    private fun generateInstanceId() {
        try {
            val file = File("application-instance.dat")
            if (file.exists()) {
                val fileReader = FileReader(file)
                val bufferedReader = BufferedReader(fileReader)
                val line = bufferedReader.readLine()
                instance = UUIDConverter.toUUID(line)
                bufferedReader.close()
            } else {
                instance = UUID.randomUUID()
                val fileWriter = FileWriter(file)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(instance.toString())
                bufferedWriter.flush()
                bufferedWriter.close()
                fileWriter.close()
            }
            logger.log(Level.INFO, "####################################################################")
            logger.log(Level.INFO, "############### {0} ###############", instance)
            logger.log(Level.INFO, "####################################################################")
        } catch (ex: IOException) {
            logger.log(Level.SEVERE, "\"############### ERROR: Cannot generate the INSTANCE ID to this application. ###############.", ex)
        }
    }
}