package io.github.adoniasalcantara.anp.puller

import io.github.adoniasalcantara.anp.model.City
import io.github.adoniasalcantara.anp.model.FuelType
import org.jsoup.Connection
import org.jsoup.Jsoup

class Puller(
    private val target: String,
    private val cookie: Pair<String, String>,
    private val weekCode: Int
) {
    fun fetch(city: City, fuelType: FuelType): String {
        return Jsoup.connect(target)
            .method(Connection.Method.POST)
            .ignoreContentType(true)
            .cookie(cookie.first, cookie.second)
            .data("COD_SEMANA", "$weekCode")
            .data("COD_MUNICIPIO", "${city.code}")
            .data("DESC_MUNICIPIO", city.name)
            .data("COD_COMBUSTIVEL", "${fuelType.code}")
            .data("DESC_COMBUSTIVEL", fuelType.name)
            .execute()
            .body()
    }
}