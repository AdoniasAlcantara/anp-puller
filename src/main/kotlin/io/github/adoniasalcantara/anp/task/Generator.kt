package io.github.adoniasalcantara.anp.task

import io.github.adoniasalcantara.anp.model.City

class Generator(private val cities: List<City>) {
    private var currentId = 0

    fun next(): Task? = synchronized(this) {
        cities.getOrNull(currentId)?.let { city -> Task(++currentId, city) }
    }
}