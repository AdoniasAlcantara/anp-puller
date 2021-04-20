package io.github.adoniasalcantara.anp.puller

import io.github.adoniasalcantara.anp.model.Fuel
import io.github.adoniasalcantara.anp.model.FuelType
import io.github.adoniasalcantara.anp.model.Station
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val sanitizeRegex = Regex("[<>!:;+-]")

fun makeSha1Key(input: String): String {
    return MessageDigest
        .getInstance("SHA-1")
        .digest(input.toByteArray())
        .fold("") { str, byte -> str + "%02x".format(byte) }
}

fun sanitize(input: String): String {
    return input
        .replace(sanitizeRegex, "")
        .removeSuffix("/")
}

fun parseDate(dateInput: String): String {
    return LocalDate
        .parse(sanitize(dateInput), dateFormatter)
        .format(DateTimeFormatter.ISO_LOCAL_DATE)
}

fun parseFuelType(fuelTypeInput: String): FuelType {
    FuelType.values().forEach {
        if (fuelTypeInput.endsWith(it.name)) return it
    }

    error("There is no fuel type associated with \"$fuelTypeInput\"")
}

fun parseCurrency(currencyInput: String): Float {
    return currencyInput
        .replace(",", ".")
        .replace("-", "")
        .toBigDecimal()
        .toFloat()
}

fun parseHtml(html: String): List<Station> {
    return parseDocument(Jsoup.parse(html))
}

fun parseDocument(document: Document): List<Station> {
    val city = document.selectFirst("table > tbody > tr > td > b")
        .text()
        .split("-")[1]

    val fuelType = document.selectFirst("body > table > tbody > tr:nth-child(2) > td > b")
        .text()
        .run(::parseFuelType)

    val rows = document.selectFirst("table.table_padrao > tbody")
        .children()
        .drop(2) // Remove headers

    return rows.map { row ->
        val data = row.children()
        val company = data[0].text()
        val brand = data[3].text()
        val address = data[1].text()
        val neighborhood = data[2].text()
        val fuel = Fuel(
            fuelType,
            parseDate(data[5].text()),
            parseCurrency(data[4].text())
        )

        return@map Station(
            makeSha1Key("$company$address$neighborhood$city$brand"),
            sanitize(company),
            sanitize(address),
            sanitize(neighborhood),
            sanitize(city),
            sanitize(brand),
            listOf(fuel)
        )
    }
}
