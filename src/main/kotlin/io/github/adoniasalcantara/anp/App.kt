package io.github.adoniasalcantara.anp

import io.github.adoniasalcantara.anp.model.City
import io.github.adoniasalcantara.anp.puller.Puller
import io.github.adoniasalcantara.anp.task.CoroutineWorker
import io.github.adoniasalcantara.anp.task.Task
import io.github.adoniasalcantara.anp.task.TaskRunner
import io.github.adoniasalcantara.anp.util.FileHandler
import io.github.adoniasalcantara.anp.util.readJson
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory.getLogger
import java.nio.file.Paths
import kotlin.system.exitProcess

private val logger = getLogger({}::class.java.packageName)

fun main(vararg args: String) = try {
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
            Paths.get(destFile)
        )
    }

    val cities: List<City> = Paths.get(config.citiesFile).run(::readJson)
    logger.debug("Found cities file: ${config.citiesFile}.")
    logger.debug("Read ${cities.count()} cities")

    val workers = List(config.concurrency) { index ->
        CoroutineWorker("Worker-$index", puller, fileHandler)
    }

    val tasks = cities.mapIndexed(::Task)
    logger.info("Starting...")
    runBlocking { TaskRunner(tasks, workers).run() }
    fileHandler.writeConcat()
} catch(exception: Throwable) {
    logger.error("Fatal error.", exception)
    exitProcess(1)
}
