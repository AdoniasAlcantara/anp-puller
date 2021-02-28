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

class CoroutineWorker(
    val name: String,
    private val puller: Puller,
    private val fileHandler: FileHandler
) {
    suspend fun run(task: Task): Int = coroutineScope {
        val (taskId, city) = task

        // Fire requests concurrently for each fuel type
        val results = FuelType.values().map { fuelType ->
            async { puller.fetch(city, fuelType) }
        }

        // Merge partial results into a single list
        val stations = results.awaitAll()
            .flatMap { html -> parseHtml(html) }
            .groupingBy { station -> station.key }
            .reduce { _, acc, cur -> acc.copy(fuels = acc.fuels + cur.fuels) }
            .values
            .toList()

        // Write merged result to a temp file
        if (stations.isNotEmpty()) withContext(IO) {
            fileHandler.writeTemp(taskId, stations)
        }

        return@coroutineScope stations.count()
    }
}