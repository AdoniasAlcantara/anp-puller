package io.github.adoniasalcantara.anp.puller

import io.github.adoniasalcantara.anp.model.City
import io.github.adoniasalcantara.anp.model.FuelType
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class Puller(
    target: String,
    cookie: Pair<String, String>,
    private val weekCode: Int
) {
    private val client = HttpClient {
        defaultRequest {
            url(target)
            cookie(cookie.first, cookie.second)
        }

        Charsets {
            responseCharsetFallback = charset("Windows-1252")
        }
    }

    suspend fun fetch(city: City, fuelType: FuelType): String {
        return client.submitForm(
            formParameters = Parameters.build {
                append("COD_SEMANA", "$weekCode")
                append("COD_MUNICIPIO", "${city.code}")
                append("DESC_MUNICIPIO", city.name)
                append("COD_COMBUSTIVEL", "${fuelType.code}")
                append("DESC_COMBUSTIVEL", fuelType.name)
            }
        )
    }
}