package io.github.adoniasalcantara.anp.task

import io.github.adoniasalcantara.anp.util.FileHelper
import io.github.adoniasalcantara.anp.model.FuelType
import io.github.adoniasalcantara.anp.model.Station
import io.github.adoniasalcantara.anp.puller.Puller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class Worker(
    private val puller: Puller,
    private val fileHelper: FileHelper
) {
    fun runAsync(supply: () -> Task?) = CoroutineScope(Default).async {
        while (true) {
            // Worker gets as many tasks as it is able to process.
            // If there are no more tasks in the queue, worker
            // leaves the loop and finishes the job.
            val (taskId, city) = supply() ?: break

            // Fire jobs concurrently for each fuel type
            val jobs = FuelType.values().map { fuelType ->
                async(IO) { puller.fetch(city, fuelType) }
            }

            // Merge all partial results into a single map
            val combinedResult = mutableMapOf<String, Station>()

            jobs.awaitAll().flatten().forEach { station ->
                combinedResult.merge(station.key, station) { old, new ->
                    old.copy(fuels = old.fuels + new.fuels)
                }
            }

            val stations = combinedResult.values.toList()

            // Write the combined result to a temp file
            withContext(IO) {
                fileHelper.writeTemp(taskId, stations)
            }
        }
    }
}
