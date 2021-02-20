package io.github.adoniasalcantara.anp

import io.github.adoniasalcantara.anp.util.readJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import java.net.URL
import java.nio.file.NoSuchFileException
import java.nio.file.Path

@Serializable
data class Config(
    val targetUrl: String,
    val cookieKey: String,
    val cookieValue: String,
    val weekCode: Int,
    val tempDir: String = System.getProperty("java.io.tmpdir"),
    val outFile: String = "./",
    val citiesFile: String = "./cities.json",
    val numWorkers: Int = 4
)

fun configError(message: String) {
    error("Configuration error: $message.")
}

fun getConfig(configFile: Path): Config {
    val config: Config = try {
        readJson(configFile)
    } catch (error: SerializationException) {
        error("Invalid configuration file. Reason: ${error.message}")
    } catch (error: NoSuchFileException) {
        error("Missing configuration file: $configFile")
    }

    runCatching { URL(config.targetUrl) }.onFailure { configError("Invalid targetUrl") }
    if (config.cookieKey.isBlank()) configError("cookieKey must not be blank")
    if (config.cookieValue.isBlank()) configError("cookieValue must not be blank")
    if (config.weekCode <= 0) configError("Invalid weekCode")
    if (config.numWorkers <= 0) configError("numWorkers should be at least 1")

    return config
}
