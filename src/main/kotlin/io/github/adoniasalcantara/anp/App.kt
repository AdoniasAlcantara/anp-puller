package io.github.adoniasalcantara.anp

import io.github.adoniasalcantara.anp.puller.Puller
import io.github.adoniasalcantara.anp.task.Task
import io.github.adoniasalcantara.anp.task.TaskRunner
import io.github.adoniasalcantara.anp.task.Worker
import io.github.adoniasalcantara.anp.util.FileHandler
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

fun main(vararg args: String) = runBlocking {
    val config = getConfig(
        Paths.get(args.getOrElse(0) { "./config.json" })
    )

    val fileHandler = config.run {
        FileHandler(
            Paths.get(tempDir),
            Paths.get(citiesFile),
            Paths.get(outFile)
        )
    }

    val puller = config.run {
        Puller(
            targetUrl,
            cookieKey to cookieValue,
            weekCode
        )
    }

    val tasks = fileHandler.readCities().mapIndexed(::Task)
    val workers = List(config.numWorkers) { Worker(puller, fileHandler) }
    TaskRunner(tasks, workers).run()
    fileHandler.writeConcat()
}