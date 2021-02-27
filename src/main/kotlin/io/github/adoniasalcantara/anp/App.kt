package io.github.adoniasalcantara.anp

import io.github.adoniasalcantara.anp.puller.Puller
import io.github.adoniasalcantara.anp.task.Task
import io.github.adoniasalcantara.anp.task.TaskRunner
import io.github.adoniasalcantara.anp.task.Worker
import io.github.adoniasalcantara.anp.util.FileHandler
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory.getLogger
import java.nio.file.Paths

private val logger = getLogger({}::class.java.packageName)

fun main(vararg args: String) = runBlocking {
    val configFile = Paths.get(args.firstOrNull() ?: "./config.json" )
    val config = getConfig(configFile)
    logger.debug("Found configuration file: $configFile.")
    logger.debug("$config")

    val puller = config.run {
        Puller(
            targetUrl,
            cookieKey to cookieValue,
            weekCode
        )
    }

    val fileHandler = config.run {
        FileHandler(
            Paths.get(tempDir),
            Paths.get(citiesFile),
            Paths.get(outFile)
        )
    }

    val cities = fileHandler.readCities()
    logger.debug("Found cities file: ${config.citiesFile}.")
    logger.debug("Read ${cities.count()} cities")
    cities.forEach { logger.trace("$it") }

    val tasks = cities.mapIndexed(::Task)
    val workers = List(config.numWorkers) { Worker(puller, fileHandler) }
    TaskRunner(tasks, workers).run()
    fileHandler.writeConcat()
}