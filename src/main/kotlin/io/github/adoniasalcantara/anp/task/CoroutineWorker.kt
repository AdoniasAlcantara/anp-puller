package io.github.adoniasalcantara.anp.task

import io.github.adoniasalcantara.anp.model.FuelType
import io.github.adoniasalcantara.anp.puller.Puller
import io.github.adoniasalcantara.anp.puller.parseHtml
import io.github.adoniasalcantara.anp.util.FileHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * Unit of work that processes a given task.
 *
 * @param name this worker's name.
 * @param puller used to make remote requests.
 * @param fileHandler used to store intermediate files.
 */
class CoroutineWorker(
    val name: String,
    private val puller: Puller,
    private val fileHandler: FileHandler
) {
    /**
     * Runs a task and returns the number of results found.
     * This function suspends when waiting for I/O operations.
     *
     * @param task the source [Task]
     * @return the number of results found.
     */
    suspend fun run(task: Task): Int = coroutineScope {
        val (taskId, city) = task

        val results = FuelType.values().map { fuelType ->
            async { puller.fetch(city, fuelType) }
        }

        val stations = results.awaitAll()
            .flatMap { html -> parseHtml(html) }
            .groupingBy { station -> station.key }
            .reduce { _, acc, cur -> acc.copy(fuels = acc.fuels + cur.fuels) }
            .values
            .toList()

        if (stations.isNotEmpty()) withContext(IO) {
            fileHandler.writeTemp(taskId, stations)
        }

        return@coroutineScope stations.count()
    }
}
