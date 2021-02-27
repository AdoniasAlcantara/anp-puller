package io.github.adoniasalcantara.anp.task

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskRunner(
    tasks: Collection<Task>,
    private val workers: Collection<CoroutineWorker>
) {
    private val iterator = tasks.iterator()

    suspend fun run() = withContext(Default) {
        workers.forEach {
            launch { dispatch(it) }
        }
    }

    private suspend fun dispatch(worker: CoroutineWorker) {
        while (true) {
            val task = nextTask() ?: break
            worker.run(task)
        }
    }

    private fun nextTask() = synchronized(iterator) {
        if (iterator.hasNext()) iterator.next() else null
    }
}
