package io.github.adoniasalcantara.anp.util

import io.github.adoniasalcantara.anp.model.City
import io.github.adoniasalcantara.anp.model.Station
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

inline fun <reified T> readJson(file: Path): T {
    val str = Files.readString(file)
    return Json.decodeFromString(str)
}

class FileHelper(
    private val tempDir: Path,
    private val citiesFile: Path,
    private val concatFile: Path
) {
    private companion object {
        const val TEMP_FILE_EXT = ".tmp.json"
    }

    private val pathMatcher by lazy {
        FileSystems.getDefault().getPathMatcher("glob:**$TEMP_FILE_EXT")
    }

    init {
        val temp = tempDir.toFile()
        if (!temp.exists()) temp.mkdir()
    }

    fun readCities(): List<City> {
        return readJson(citiesFile)
    }

    fun writeTemp(taskId: Int, stations: List<Station>) {
        val str = Json.encodeToString(stations)
        val tempFile = tempDir.resolve("$taskId$TEMP_FILE_EXT")
        Files.writeString(tempFile, str)
    }

    fun writeConcat() {
        val tempFiles = Files
            .find(tempDir, 1, { file, _ -> pathMatcher.matches(file) })
            .toList()

        Files.newBufferedWriter(concatFile).use { writer ->
            writer.write("[")

            tempFiles.forEachIndexed { index, file ->
                val content = Files.readString(file).trimStart('[').trimEnd(']')
                writer.write(content)

                if (index < tempFiles.lastIndex) writer.write(",")
            }

            writer.write("]")
        }
    }
}