package io.github.adoniasalcantara.anp.task

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory.getLogger

private val logger = getLogger(TaskRunner::class.java)

/**
 * Responsible for coordinating [workers] and distributing
 * [tasks] in a concurrent fashion. The number of workers is
 * the same as the maximum number of tasks processed
 * simultaneously.
 *
 * @param tasks the tasks to be processed.
 * @param workers a pool of workers used to process that tasks.
 */
class TaskRunner(
    private val tasks: Collection<Task>,
    private val workers: Collection<CoroutineWorker>
) {
    private val iterator = tasks.iterator()

    /**
     * Runs tasks concurrently in the [Default] coroutine context
     * and returns the total number of results found.
     *
     * @return the total of results found.
     */
    suspend fun run() = withContext(Default) {
        logger.debug("Running ${tasks.count()} task(s) using ${workers.count()} worker(s).")
        tasks.forEach { logger.trace("$it") }

        val results = workers.map { worker ->
            async { dispatch(worker) }
        }

        logger.info("Total found: ${results.awaitAll().sum()}")
    }

    private suspend fun dispatch(worker: CoroutineWorker): Int {
        var totalResults = 0
        logger.debug("${worker.name} started.")

        while (true) {
            val task = nextTask() ?: break
            logger.debug("${worker.name} assigned to $task.")

            val results = worker.run(task)
            totalResults += results
            logger.info("${task.city.name}: $results results.")
        }

        logger.debug("${worker.name} finished with $totalResults results found.")
        return totalResults
    }

    private fun nextTask() = synchronized(iterator) {
        if (iterator.hasNext()) iterator.next() else null
    }
}
