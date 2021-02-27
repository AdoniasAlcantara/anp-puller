package io.github.adoniasalcantara.anp.task

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory.getLogger

private val logger = getLogger(TaskRunner::class.java)

class TaskRunner(
    private val tasks: Collection<Task>,
    private val workers: Collection<CoroutineWorker>
) {
    private val iterator = tasks.iterator()

    suspend fun run() = withContext(Default) {
        logger.debug("Running ${tasks.count()} task(s) using ${workers.count()} worker(s).")
        tasks.forEach { logger.trace("$it") }

        workers.forEach {
            launch { dispatch(it) }
        }
    }

    private suspend fun dispatch(worker: CoroutineWorker) {
        logger.debug("${worker.name} started.")

        while (true) {
            val task = nextTask() ?: break
            logger.debug("${worker.name} assigned to $task")
            worker.run(task)
        }

        logger.debug("${worker.name} finished.")
    }

    private fun nextTask() = synchronized(iterator) {
        if (iterator.hasNext()) iterator.next() else null
    }
}
