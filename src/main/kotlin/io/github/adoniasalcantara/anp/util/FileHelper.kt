package io.github.adoniasalcantara.anp.util

import io.github.adoniasalcantara.anp.model.City
import io.github.adoniasalcantara.anp.model.Station
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

class FileHelper(
    private val tempDir: Path,
    private val citiesFile: Path,
    private val concatFile: Path
) {

    fun readCities(): List<City> {
        val str = Files.readString(citiesFile)
        return Json.decodeFromString(str)
    }

    fun writeTemp(taskId: Int, stations: List<Station>) {
        TODO("Not implemented")
    }

    fun writeConcat() {
        TODO("Not implemented")
    }
}