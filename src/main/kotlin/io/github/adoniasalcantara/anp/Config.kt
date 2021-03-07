package io.github.adoniasalcantara.anp

import io.github.adoniasalcantara.anp.util.makeTempDir
import io.github.adoniasalcantara.anp.util.readJson
import io.github.adoniasalcantara.anp.util.timestamp
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import java.net.URL
import java.nio.file.NoSuchFileException
import java.nio.file.Path

/**
 * Represents a configuration file that contains essential properties
 * for initializing other objects.
 *
 * @property targetUrl URL used to send requests to ANP's website..
 * @property cookieKey key of the cookie used to access the ANP's website.
 * @property cookieValue value of the cookie used to access the ANP's website.
 * @property weekCode week number referring to ANP data collection.
 * @property tempDir directory where temporary files are stored.
 * @property destFile destination file containing the final result.
 * @property citiesFile file containing the cities to be fetched.
 * @property numWorkers number of simultaneously performed tasks.
 */
@Serializable
data class Config(
    val targetUrl: String,
    val cookieKey: String,
    val cookieValue: String,
    val weekCode: Int,
    val tempDir: String = makeTempDir(),
    val destFile: String = "./stations_${timestamp()}.json",
    val citiesFile: String = "./cities.json",
    val numWorkers: Int = 4
)

fun configError(message: String) {
    error("Configuration error: $message.")
}

/**
 * Reads the configuration file and returns a [Config] object.
 *
 * @param configFile the configuration file path.
 * @return [Config] object that represents that file.
 * @throws NoSuchFileException if the file does not exist.
 * @throws IllegalStateException if the file contents are invalid.
 */
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
